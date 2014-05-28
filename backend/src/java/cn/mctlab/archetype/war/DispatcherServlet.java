/**
 * Created by IntelliJ IDEA
 * User: Uraka.Lee
 * Date: 2013-07-21
 *
 * Copyleft 2000 MCTLab.cn
 */
package cn.mctlab.archetype.war;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 *
 */
public class DispatcherServlet extends org.springframework.web.servlet.DispatcherServlet {

    //-- public finals --//
    //-- private finals --//

    private static final Logger LOG = LoggerFactory.getLogger(DispatcherServlet.class);

    //-- properties --//
    //-- constructors --//
    //-- destructors --//
    //-- implements --//

    @Override
    public void init(ServletConfig config) throws ServletException {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        super.init(config);
    }

    //-- un-implements --//
    //-- methods --//
    //-- functions --//
    //-- utils --//
    //-- getters & setters --//
    //-- inner classes --//
}
