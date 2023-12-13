package com.echo.framework.platform.interceptor.session;

import com.echo.framework.platform.App;
import com.echo.framework.platform.interceptor.CookieInterceptor;
import com.echo.framework.platform.interceptor.session.provider.LocalSessionProvider;
import com.echo.framework.platform.interceptor.session.provider.SessionProvider;
import com.echo.framework.util.ControllerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


public class SessionInterceptor extends HandlerInterceptorAdapter implements BeanFactoryAware {
    private static final String ATTRIBUTE_CONTEXT_INITIALIZED = SessionInterceptor.class.getName() + ".CONTEXT_INITIALIZED";
    private static final String BEAN_NAME_SESSION_PROVIDER = "sessionProvider";

    private final Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);

    private final App.Session sessionSetting;

    private SessionProvider sessionProvider;

    private DefaultListableBeanFactory beanFactory;

    public SessionInterceptor(App.Session sessionSetting) {
        this.sessionSetting = sessionSetting;
    }

    @Autowired
    private SessionIdProcessor sessionIdProcessor;
    @Autowired
    private SessionContext sessionContext;
    @Autowired
    private SecureSessionContext secureSessionContext;
    @Autowired
    private CookieInterceptor cookieInterceptor;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isTrue(beanFactory instanceof DefaultListableBeanFactory, "bean factory not set.");
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @PostConstruct
    public void initialize() {
        registerSessionProvider();
        sessionIdProcessor.generateSessionIdKey();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!requireSession(handler)) return true;

        // only process non-forwarded request, to make sure only init once per request
        if (!initialized(request)) {
            logger.debug("initialize sessionContext");

            Assert.isTrue(cookieInterceptor.initialized(request), "sessionInterceptor depends on cookieInterceptor, please check WebConfig");

            loadSession(sessionContext, false);
            if (request.isSecure()) {
                secureSessionContext.underSecureRequest();
                loadSession(secureSessionContext, true);
            }
            request.setAttribute(ATTRIBUTE_CONTEXT_INITIALIZED, Boolean.TRUE);
        }
        return true;
    }

    private boolean initialized(HttpServletRequest request) {
        Boolean initialized = (Boolean) request.getAttribute(ATTRIBUTE_CONTEXT_INITIALIZED);
        return Boolean.TRUE.equals(initialized);
    }

    private boolean requireSession(Object handler) {
        return ControllerHelper.findMethodOrClassLevelAnnotation(handler, RequireSession.class) != null;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        saveAllSessions(request);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // if some interceptor break the preHandle by returning false, all postHandle will be skipped.
        // by this way we want to try to save session on completion if possible
        // due to setCookies only works before view is rendered
        saveAllSessions(request);
    }

    private void saveAllSessions(HttpServletRequest request) {
        saveSession(sessionContext, false);
        if (request.isSecure()) {
            saveSession(secureSessionContext, true);
        }
    }

    private void loadSession(SessionContext sessionContext, boolean secure) {
        String sessionId = sessionIdProcessor.get(secure);
        if (sessionId != null) {
            String sessionData = sessionProvider.getAndRefreshSession(sessionId);
            if (sessionData != null) {
                sessionContext.setId(sessionId);
                sessionContext.loadSessionData(sessionData);
            } else {
                generateNewSessionId(sessionContext, secure);
            }
        } else {
            generateNewSessionId(sessionContext, secure);
        }
    }


    private void generateNewSessionId(SessionContext sessionContext, boolean secure) {
        logger.debug("can not find session, generate new sessionId to replace old one");
        sessionContext.requireNewSessionId();
        String sessionId = UUID.randomUUID().toString();
        sessionContext.setId(sessionId);
        sessionIdProcessor.set(secure, sessionId);
    }

    private void saveSession(SessionContext sessionContext, boolean secure) {
        if (sessionContext.changed()) {
            if (sessionContext.invalidated()) {
                deleteSession(sessionContext, secure);
            } else {
                persistSession(sessionContext);
            }
            sessionContext.saved();
        }
    }

    private void deleteSession(SessionContext sessionContext, boolean secure) {
        String sessionId = sessionContext.getId();
        if (sessionId == null) {
            // session was not persisted, nothing is required
            return;
        }
        sessionProvider.clearSession(sessionId);
        sessionIdProcessor.delete(secure);
    }

    private void persistSession(SessionContext sessionContext) {
        sessionProvider.saveSession(sessionContext.getId(), sessionContext.getSessionData());
    }

    private void registerSessionProvider() {
        SessionProviderType type = sessionSetting.getType();
        if (SessionProviderType.LOCAL.equals(type)) {
            beanFactory.registerSingleton(BEAN_NAME_SESSION_PROVIDER, new LocalSessionProvider(sessionSetting));
        } else if (SessionProviderType.REDIS.equals(type)) {
            assert beanFactory.containsBeanDefinition(BEAN_NAME_SESSION_PROVIDER);
        } else {
            throw new IllegalStateException("unsupported session provider type, type=" + type);
        }
        sessionProvider = beanFactory.getBean(SessionProvider.class);
    }
}
