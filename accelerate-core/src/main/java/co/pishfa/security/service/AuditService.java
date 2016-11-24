/**
 * 
 */
package co.pishfa.security.service;

import co.pishfa.accelerate.async.Async;
import co.pishfa.accelerate.async.RescheduleType;
import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.common.AuditableEvent;
import co.pishfa.accelerate.core.ConfigAppliedEvent;
import co.pishfa.accelerate.i18n.domain.Locale;
import co.pishfa.accelerate.notification.Notification;
import co.pishfa.accelerate.notification.service.NotificationService;
import co.pishfa.accelerate.schedule.ScheduleInterval;
import co.pishfa.accelerate.schedule.ScheduleTrigger;
import co.pishfa.accelerate.schedule.Scheduled;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.ui.UiService;
import co.pishfa.accelerate.utility.CommonUtils;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.accelerate.utility.UriUtils;
import co.pishfa.security.entity.audit.Audit;
import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.entity.audit.Auditable;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authentication.User;
import co.pishfa.security.entity.authorization.Action;
import co.pishfa.security.repo.ActionRepo;
import co.pishfa.security.repo.AuditRepo;
import co.pishfa.security.repo.DomainRepo;
import co.pishfa.security.repo.UserRepo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.quartz.SchedulerException;
import org.slf4j.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static co.pishfa.accelerate.utility.TimeUtils.*;

/**
 * @author Taha Ghasemi
 * 
 */
@Service
public class AuditService implements Serializable {

    private static final long serialVersionUID = 1L;

	@Inject
	private Logger log;

	@Inject
	private UserRepo userRepo;

	private AuditConfig auditConfig;

	@Inject
	private Identity identity;

    @Inject
    private AuthorizationService authorizationService;

	private Locale auditLocale;

	/**
	 * Audits waiting to be persisted. Might be null in case of no scheduling.
	 */
	private List<Audit> toBeAdded;

	@Inject
	private AuditRepo auditRepo;

	@Inject
	private ActionRepo actionRepo;

	@Inject
	private DomainRepo domainRepo;

    @Inject
    private UiService uiService;

	@Inject
	private NotificationService notificationService;

    private ThreadLocal<Boolean> rootAudited = new ThreadLocal<>();

    private Set<Action> excludes;

	public static AuditService getInstance() {
		return CdiUtils.getInstance(AuditService.class);
	}

	public void configSchedule(@Observes final ConfigAppliedEvent event) throws SchedulerException {
        AuditConfig newConfig = event.getConfig().getObject(AuditConfig.class);
        if(auditConfig == null || auditConfig.getFlushInterval() != newConfig.getFlushInterval()) {
            if(toBeAdded != null) {
                synchronized (toBeAdded) {
                    applyDelayedCommands(); // save any pending audits
                    schedule(newConfig);
                }
            } else
                schedule(newConfig);
        }
        Set<Action> excludes = new HashSet<>();
        for(Action action : newConfig.getExcludes()) {
            excludes.add(action);
            addChildren(excludes, action);
        }
        this.excludes = excludes; //now change
        auditConfig = newConfig;
	}

    private void addChildren(Set<Action> excludes, Action action) {
        for(Action child : action.getChildren()) {
            excludes.add(child);
            addChildren(excludes, child);
        }
    }

    private void schedule(AuditConfig newConfig) {
        long flushInterval = newConfig.getFlushInterval();
        if (flushInterval > 0) {
            if(toBeAdded == null)
                toBeAdded = new ArrayList<>();
            getInstance().runDelayedCommands(toMilliSecond(flushInterval, TimeUnit.SECONDS));
        } else {
            toBeAdded = null;
        }
    }

    public void onEvent(@Observes final AuditableEvent event) {
		event.audit();
	}

	public void audit(final Object target, final String actionName, final String message, final AuditLevel level) {
		try {
			Action action = authorizationService.findAction(actionName);
            Validate.notNull(action, "No action (or generalized action) found with name " + actionName);
			Audit audit = createAudit(target, action, message, level);
			add(audit);
		} catch (Exception e) {
			log.error("Exception when auditing", e);
		}
	}

	public void audit(final Object target, final Action action, final String message, final AuditLevel level) {
		try {
			Audit audit = createAudit(target, action, message, level);
			add(audit);
		} catch (Exception e) {
			log.error("Exception when auditing", e);
		}
	}

	public Audit createAudit(final Object target, final Action action, final String message, final AuditLevel level) {
        if (level.getOrder() < auditConfig.getLevelThreshold().getOrder() || excludes.contains(action))
            return null;

		Audit audit = new Audit();
        HttpServletRequest request = uiService.getRequest();
		if (request != null) {
			audit.setHost(request.getRemoteAddr());
			audit.setPath(UriUtils.getCurrentUrl());
		}
		audit.setAction(action);
		audit.setLevel(level);
		audit.setMessage(StringUtils.abbreviate(message, 12000));
		audit.setCreationTime(new Date());
		// TODO use auditLocale
		//audit.setFinishTime(new ExtendedLocaleTime(new Date()));

		User user = null;
		try {
			user = identity.getUser();
		} catch (Exception e) {} //sometimes SessionContext is not available
		if (user != null) {
			audit.setCreatedBy(user);
			audit.setDomain(user.getDomain());
		} else {
			audit.setCreatedBy(userRepo.findSystemUser());
			audit.setDomain(domainRepo.getMainDomain());
		}
        if (target != null && target instanceof Auditable) {
            ((Auditable) target).audit(audit);
        }
		return audit;
	}

	public void add(final Audit audit) {
        if(audit == null)
            return;

		if (toBeAdded == null) {
			auditRepo.add(audit);
		} else {
			synchronized (toBeAdded) {
				toBeAdded.add(audit);
			}
		}
		for(AuditNotificationConfig notificationConfig : auditConfig.getNotifications()) {
			if(notificationConfig.isEnabled() && notificationConfig.getIncludes().contains(audit.getAction()) && notificationConfig.getTargets().size()>0) {
				Notification notification = new Notification();
				notification.setTo(notificationConfig.getTargets());
				notification.setFrom("audit");
				notification.setTitle(audit.getAction().getTitle());
				notification.setMessage("notification.audit");

				notification.setParameters(audit.getAction().getTitle(),audit.getCreatedBy()!=null?audit.getCreatedBy().getTitle():"", audit.getTargetTitle()==null?"":(" " + audit.getTargetTitle()), StrUtils.defaultIfNull(audit.getMessage(),""));
				notificationService.notify(notification,notificationConfig.getNotifier());
			}
		}

	}

	@Async(reschedule = RescheduleType.DELETE_PREV)
	public void runDelayedCommands(@ScheduleInterval final Long interval) {
		applyDelayedCommands();
	}

	@Transactional
	public void applyDelayedCommands() {
		synchronized (toBeAdded) {
			if (!toBeAdded.isEmpty()) {
				auditRepo.add(toBeAdded);
				toBeAdded.clear();
			}
		}
	}

    @Transactional
	public void cleanUp(@Observes @Scheduled("every.month") final ScheduleTrigger t) {
		auditRepo.deleteOlderThan(toDate(since(toMilliSecond(auditConfig.getDeletePeriod(),
                TimeUnit.DAYS))));
	}

	public Locale getAuditLocale() {
		return auditLocale;
	}

    public boolean isRootAudited() {
        return CommonUtils.toBoolean(rootAudited.get());
    }

    public void setRootAudited(boolean value) {
        rootAudited.set(value);
    }

    public void removeRootAudited() {
        rootAudited.remove();
    }

}
