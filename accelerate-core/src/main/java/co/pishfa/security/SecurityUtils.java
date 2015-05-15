/**
 * 
 */
package co.pishfa.security;

import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.pishfa.accelerate.resource.ResourceUtils;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.accelerate.utility.UriUtils;
import co.pishfa.security.service.AuditService;
import co.pishfa.security.entity.audit.AuditLevel;

/**
 * Security related utility methods such as hashing and encryption
 * 
 * @author Taha Ghasemi
 * 
 */
public final class SecurityUtils {

	private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

	/**
	 * Convenience method to hash
	 * 
	 * @param data
	 *            data to hash
	 */
	public static String hash(String data) {
		return DigestUtils.sha1Hex(data);
	}

	private static Cipher tokenCipher = null;

	public static Cipher getCommonCipher() {
		if (tokenCipher == null) {
			try {
				Key key = new SecretKeySpec("post@Post_984375!".getBytes(), "AES");
				tokenCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
				tokenCipher.init(Cipher.ENCRYPT_MODE, key);
			} catch (Exception e) {
				log.error("Error init the token cipher", e);
			}
		}
		return tokenCipher;
	}

	public static String encrypt(String token) throws GeneralSecurityException {
		return new String(getCommonCipher().doFinal(token.getBytes()));
	}

	public static String escapeHtml(String input) {
		return StringEscapeUtils.escapeHtml4(input);
	}

	public static String removeHtml(String original) {
		if (original == null) {
			return null;
		}
		return original.replaceAll("\\<([a-zA-Z]|/){1}?.*?\\>", "");
	}

	private static AntiSamy antiSamy;

	public static String removeXSS(String str, boolean auditInjection) {
		if (StrUtils.isEmpty(str)) {
			return str;
		}

		try {
			if (antiSamy == null) {
				Policy policy = Policy.getInstance(ResourceUtils.getResource("antisamy.xml"));
				antiSamy = new AntiSamy(policy);
			}
			String res = antiSamy.scan(str, AntiSamy.SAX).getCleanHTML();
			if (auditInjection && !str.equals(res)) {
				AuditService.getInstance().audit(str, "injecting", res, AuditLevel.WARN);
			}
			return res;
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
	}

	public static String sanitizeUri(String uri) {
		return UriUtils.sanitizeUri(uri);
	}

}
