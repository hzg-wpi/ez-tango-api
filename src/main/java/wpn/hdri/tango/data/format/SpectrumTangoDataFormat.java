/*
 * The main contributor to this project is Institute of Materials Research,
 * Helmholtz-Zentrum Geesthacht,
 * Germany.
 *
 * This project is a contribution of the Helmholtz Association Centres and
 * Technische Universitaet Muenchen to the ESS Design Update Phase.
 *
 * The project's funding reference is FKZ05E11CG1.
 *
 * Copyright (c) 2012. Institute of Materials Research,
 * Helmholtz-Zentrum Geesthacht,
 * Germany.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package wpn.hdri.tango.data.format;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.esrf.TangoDs.TangoConst;
import wpn.hdri.tango.data.type.TangoDataType;
import wpn.hdri.tango.data.type.TangoDataTypes;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 05.06.12
 */
public final class SpectrumTangoDataFormat<T> extends TangoDataFormat<T> {
    private static final BiMap<Integer, Integer> typesMapping = HashBiMap.create();

    public SpectrumTangoDataFormat(int alias, String strAlias) {
        super(alias, strAlias);
    }

    static {
        typesMapping.put(TangoConst.Tango_DEV_STRING, TangoConst.Tango_DEVVAR_STRINGARRAY);
        typesMapping.put(TangoConst.Tango_DEV_DOUBLE, TangoConst.Tango_DEVVAR_DOUBLEARRAY);
        typesMapping.put(TangoConst.Tango_DEV_FLOAT, TangoConst.Tango_DEVVAR_FLOATARRAY);
        //TODO other
    }

    /**
     * Looks for any possible match Scalar -> Spectrum, Spectrum -> Scalar
     *
     * @param devDataType alias ({@link TangoConst}.Tango_DEV_XXX)
     * @return TangoDataType
     * @throws NullPointerException if no mapping was found
     */
    @Override
    public TangoDataType<T> getDataType(int devDataType) {
        Integer typeMapping = typesMapping.get(devDataType);
        if (typeMapping == null) {
            typeMapping = typesMapping.inverse().get(devDataType);
        }
        Preconditions.checkNotNull(typeMapping);
        return TangoDataTypes.forTangoDevDataType(typeMapping);
    }
}
