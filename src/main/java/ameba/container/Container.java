package ameba.container;

import ameba.core.Application;
import ameba.container.server.Connector;
import ameba.event.Event;
import ameba.event.EventBus;
import ameba.event.SystemEventBus;
import ameba.util.ClassUtils;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.server.ServerContainer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author icode
 */
public abstract class Container {
    public static final Logger logger = LoggerFactory.getLogger(Container.class);

    protected Application application;

    public static class StartEvent implements Event {

    }

    public Container(Application application) {
        this.application = application;
        configureHttpServer();
        configureWebSocketContainerProvider();
        application.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(getWebSocketContainerProvider()).to(ServerContainer.class).proxy(false);
                bindFactory(new Factory<Container>() {
                    @Override
                    public Container provide() {
                        return Container.this;
                    }

                    @Override
                    public void dispose(Container instance) {

                    }
                }).to(Container.class).proxy(false);
            }
        });
        configureHttpContainer();
    }

    @SuppressWarnings("unchecked")
    public static Container create(Application application) throws IllegalAccessException, InstantiationException {

        String provider = (String) application.getProperty("app.container.provider");

        try {
            Class<Container> ContainerClass = (Class<Container>) ClassUtils.getClass(provider);
            Constructor<Container> constructor = ContainerClass.<Container>getDeclaredConstructor(Application.class);
            return constructor.newInstance(application);
        } catch (InvocationTargetException e) {
            throw new ContainerException(e);
        } catch (NoSuchMethodException e) {
            throw new ContainerException(e);
        } catch (ClassNotFoundException e) {
            throw new ContainerException(e);
        } finally {
            logger.debug("HTTP容器为 {}", provider);
        }
    }

    public Application getApplication() {
        return application;
    }

    public abstract ServiceLocator getServiceLocator();

    protected abstract void configureHttpServer();

    protected abstract void configureHttpContainer();

    public abstract ServerContainer getWebSocketContainer();

    protected abstract void configureWebSocketContainerProvider();

    protected abstract WebSocketContainerProvider getWebSocketContainerProvider();

    public void start() throws Exception {
        SystemEventBus.publish(new StartEvent());
        doStart();
    }

    protected abstract void doStart() throws Exception;

    public abstract void shutdown() throws Exception;

    public abstract List<Connector> getConnectors();

    public abstract String getType();

    public abstract class WebSocketContainerProvider implements Factory<ServerContainer> {

        @Override
        public ServerContainer provide() {
            return getWebSocketContainer();
        }

        @Override
        public abstract void dispose(ServerContainer serverContainer);
    }

}
