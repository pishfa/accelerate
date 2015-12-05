package co.pishfa.security.service;

import co.pishfa.accelerate.async.Async;
import co.pishfa.accelerate.async.RescheduleType;
import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.core.ConfigAppliedEvent;
import co.pishfa.accelerate.core.FrameworkStartedEvent;
import co.pishfa.accelerate.service.BaseEntityService;
import co.pishfa.accelerate.service.Action;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.schedule.ScheduleInterval;
import co.pishfa.accelerate.ui.UiUtils;
import co.pishfa.security.LoggedInEvent;
import co.pishfa.security.LoggedOutEvent;
import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authentication.OnlineUser;
import co.pishfa.security.entity.authentication.User;
import co.pishfa.security.repo.OnlineUserRepo;
import co.pishfa.security.repo.UserRepo;
import org.apache.deltaspike.core.api.lifecycle.Destroyed;
import org.apache.deltaspike.core.api.lifecycle.Initialized;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.quartz.SchedulerException;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
@Service
public class OnlineUserService extends BaseEntityService<OnlineUser, Long> {

	private static final String ONLINE_USER_INVALIDATE_ACTION = "onlineUser.invalidate";

	private final List<Long> toBeRemoved = new ArrayList<>();

	@Inject
	private OnlineUserRepo onlineUserRepo;

	@Inject
	private Identity identity;

	@Inject
	private UserRepo userRepo;

    private SecurityConfig.OnlineUserConfig onlineUserConfig;

	/**
	 * Cached guest user
	 */
	private User guest;

	@Override
	public OnlineUserRepo getRepository() {
		return onlineUserRepo;
	}

	public static OnlineUserService getInstance() {
		return CdiUtils.getInstance(OnlineUserService.class);
	}

	public OnlineUser add(User user, String sessionId) throws Exception {
		OnlineUser u = new OnlineUser(user, sessionId);
		return add(u);
	}

	public boolean isOnline(User user) {
		applyDelayedCommands();
		return onlineUserRepo.getCountByUser(user) > 0;
	}

	public void onStart(@Observes FrameworkStartedEvent event) {
		onlineUserRepo.deleteAll();
		guest = userRepo.findGuest();
	}

	public void onConfigurationChange(@Observes ConfigAppliedEvent event) throws SchedulerException {
        SecurityConfig.OnlineUserConfig newConfig = event.getConfig().getObject(SecurityConfig.OnlineUserConfig.class);
        if(onlineUserConfig == null || onlineUserConfig.getUpdateInterval() != newConfig.getUpdateInterval()) {
            getInstance().runDelayedCommands(newConfig.getUpdateInterval() * 1000L);
        }
        onlineUserConfig = newConfig;
	}

	public void onLogin(@Observes LoggedInEvent event) throws Exception {
		// if after login a new session is created...
		if (identity.getOnlineUser() == null) {
			HttpSession session = UiUtils.getSession();
			OnlineUser onlineUser = add(event.getUser(), session.getId());
			identity.setOnlineUser(onlineUser);
			identity.putInSession(session);
		} else
			identity.setUser(event.getUser());
		identity.setLoggedIn(true);
		identity.setOnlineUser(onlineUserRepo.edit(identity.getOnlineUser()));
	}

	public void onLogout(@Observes LoggedOutEvent event) {
        identity.setOnlineUser(onlineUserRepo.findById(identity.getOnlineUser().getId()));
		identity.setUser(guest);
		identity.setLoggedIn(false);
		identity.setOnlineUser(onlineUserRepo.edit(identity.getOnlineUser()));
	}

	public void onSessionCreated(@Observes @Initialized HttpSession session) throws Exception {
		OnlineUser onlineUser = add(guest, session.getId());
		identity.setOnlineUser(onlineUser);
		identity.putInSession(session);
	}

	public void onSessionDestroyed(@Observes @Destroyed HttpSession session) {
		Identity identity = Identity.getFromSession(session);
		if (identity != null) {
			OnlineUser onlineUser = identity.getOnlineUser();
			delete(onlineUser);
		}
	}

	@Override
	public void delete(OnlineUser onlineUser) {
		if (onlineUser != null) {
			synchronized (toBeRemoved) {
				toBeRemoved.add(onlineUser.getId());
			}
		}
	}

	@Async(reschedule = RescheduleType.DELETE_PREV)
	public void runDelayedCommands(@ScheduleInterval Long interval) {
		applyDelayedCommands();
	}

	@Transactional
	public void applyDelayedCommands() {
		synchronized (toBeRemoved) {
			if (!toBeRemoved.isEmpty()) {
				for(Long id : toBeRemoved) {
					try {
						OnlineUser ou = findById(id);
						onlineUserRepo.delete(ou);
					} catch(Exception e) {}
				}
				toBeRemoved.clear();
			}
		}
	}

	public void invalidate(String[] userNames) {
		List<OnlineUser> onlineUsers = onlineUserRepo.findByUserNames(Arrays.asList(userNames));
		for (OnlineUser onlineUser : onlineUsers) {
			invalidate(onlineUser);
		}
		AuditService.getInstance().audit(userNames, ONLINE_USER_INVALIDATE_ACTION, null, AuditLevel.INFO);
	}

	@Action
	public void invalidate(@NotNull OnlineUser onlineUser) {
		onlineUser.getSession().invalidate();
		onlineUser.setSessionId(null);
		delete(onlineUser);
	}

}