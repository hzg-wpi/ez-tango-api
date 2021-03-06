// +======================================================================
//   $Source$
//
//   Project:   ezTangORB
//
//   Description:  java source code for the simplified TangORB API.
//
//   $Author: Igor Khokhriakov <igor.khokhriakov@hzg.de> $
//
//   Copyright (C) :      2014
//                        Helmholtz-Zentrum Geesthacht
//                        Max-Planck-Strasse, 1, Geesthacht 21502
//                        GERMANY
//                        http://hzg.de
//
//   This file is part of Tango.
//
//   Tango is free software: you can redistribute it and/or modify
//   it under the terms of the GNU Lesser General Public License as published by
//   the Free Software Foundation, either version 3 of the License, or
//   (at your option) any later version.
//
//   Tango is distributed in the hope that it will be useful,
//   but WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//   GNU Lesser General Public License for more details.
//
//   You should have received a copy of the GNU Lesser General Public License
//   along with Tango.  If not, see <http://www.gnu.org/licenses/>.
//
//  $Revision: 25721 $
//
// -======================================================================

package org.tango.client.ez.proxy;

import fr.esrf.TangoApi.DeviceProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 29.08.13
 */
public class TangoProxies {
    private TangoProxies() {
    }

    public static TangoProxy newDeviceProxyWrapper(String url) throws TangoProxyException {
        return new DeviceProxyWrapper(url);
    }

    public static TangoProxy newDeviceProxyWrapper(DeviceProxy proxy) throws TangoProxyException {
        return new DeviceProxyWrapper(proxy);
    }

    public static <T> T newTangoProxy(final String device, Class<T> clazz) throws TangoProxyException {
        //TODO check device and interface compatibility, i.e. clazz is the class of the device

        InvocationHandler handler = new InvocationHandler() {
            final TangoProxy tangoProxy = new DeviceProxyWrapper(device);

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws TangoProxyException {
                String methodName = method.getName();

                try {
                    if (tangoProxy.hasCommand(methodName))
                        return tangoProxy.executeCommand(methodName, args != null ? args[0] : null);
                    else if (methodName.startsWith("get"))
                        return tangoProxy.readAttribute(methodName.substring(3));
                    else if (methodName.startsWith("is"))
                        return tangoProxy.readAttribute(methodName.substring(2));
                    else if (methodName.startsWith("set"))
                        tangoProxy.writeAttribute(methodName.substring(3), args != null ? args[0] : null);
                    else
                        throw new TangoProxyException(tangoProxy.getName(), "Has neither command nor attribute " + methodName);
                } catch (NoSuchCommandException e) {
                    throw new AssertionError(e);
                } catch (NoSuchAttributeException e) {
                    throw new AssertionError(e);
                }

                return null;
            }
        };

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
    }
}
