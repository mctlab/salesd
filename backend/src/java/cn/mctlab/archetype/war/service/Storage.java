/**
 * Created by IntelliJ IDEA
 * User: Uraka.Lee
 * Date: 2013-07-01
 *
 * Copyleft 2000 MCTLab.cn
 */
package cn.mctlab.archetype.war.service;

import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class Storage implements IStorage {

    //-- public finals --//
    //-- private finals --//
    //-- properties --//
    //-- constructors --//
    //-- destructors --//
    //-- implements --//

    @Override
    public String response() {
        return "Storage";
    }

    //-- un-implements --//
    //-- methods --//
    //-- functions --//
    //-- utils --//
    //-- getters & setters --//
    //-- inner classes --//
}
