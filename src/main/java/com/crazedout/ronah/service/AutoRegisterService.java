package com.crazedout.ronah.service;
/*
 * Ronah REST Server
 * Copyright (c) 2026 Fredrik Roos.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * mail: info@crazedout.com
 */

import com.crazedout.ronah.Repository;

/**
 * Abstract Service class that automatically register itself to Repository.
 */
public abstract class AutoRegisterService extends Service {

    protected boolean active = true;

    /**
     * Creates a AutoRegisterService
     */
    public AutoRegisterService() {
        Repository.addService(this);
    }


    /**
     * Registers a new Service to the Repository.
     * @param c Class AutoregisterService
     * @return Object AutoRegisterService
     */
    public static AutoRegisterService register(Class<? extends AutoRegisterService> c){
        try {
            return c.getDeclaredConstructor().newInstance();
        }catch(Exception ex){
            ex.printStackTrace(System.out);
        }
        return null;
    }

    @Override
    public String getName(){
        String clazz = getClass().getName();
        return clazz.substring(clazz.lastIndexOf(".")+1);
    }

    public void setActive(boolean active){
        this.active=active;
    }

    @Override
    public String getPurpose(){
        return "Apply REST service to the world.";
    }

}