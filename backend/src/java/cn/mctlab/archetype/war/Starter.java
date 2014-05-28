/**
 * Created by IntelliJ IDEA
 * User: Uraka.Lee
 * Date: 2013-07-01
 *
 * Copyleft 2000 MCTLab.cn
 */
package cn.mctlab.archetype.war;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.mctlab.archetype.war.controller.IndexController;

/**
 *
 */
public class Starter {

    //-- public finals --//
    //-- private finals --//

    private static final Logger LOG = LoggerFactory.getLogger(Starter.class);

    //-- properties --//
    //-- constructors --//
    //-- destructors --//
    //-- implements --//

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring/appContext-run.xml");
        IndexController controller = (IndexController) ctx.getBean("indexController");
        System.out.println(controller.test());
        LOG.info(controller.test());
    }

    //-- un-implements --//
    //-- methods --//
    //-- functions --//
    //-- utils --//
    //-- getters & setters --//
    //-- inner classes --//
}
