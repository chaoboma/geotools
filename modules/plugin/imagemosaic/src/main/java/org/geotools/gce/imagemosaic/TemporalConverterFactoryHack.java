/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.gce.imagemosaic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotools.util.Converter;
import org.geotools.util.ConverterFactory;
import org.geotools.util.factory.Hints;

/**
 * Converter factory which created converting between temporal types and {@link String}
 *
 * <p>Supported save conversions:
 *
 * <ul>
 *   <li>{@link java.util.Date} to {@link String}
 *   <li>{@link java.sql.Time} to {@link to {@link String}}
 *   <li>{@link java.util.Date} to {@link to {@link String}}
 *   <li>{@link java.util.Calendar} to {@link to {@link String}}
 *   <li>{@link XMLGregorianCalendar} to {@link to {@link String}}
 * </ul>
 *
 * <p>The hint {@link ConverterFactory#SAFE_CONVERSION} is used to control which conversions will be applied.
 *
 * @author Simone Giannecchini, GeoSolutions
 * @since 9.0
 */
class TemporalConverterFactoryHack implements ConverterFactory {

    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    @Override
    public Converter createConverter(Class source, Class target, Hints hints) {

        if (Date.class.isAssignableFrom(source)) {

            // target is string
            if (String.class.equals(target)) {
                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(TimeZone.getTimeZone("UTC")); // we DO work only with UTC times

                return new Converter() {
                    @Override
                    public <T> T convert(Object source, Class<T> target) throws Exception {
                        if (source instanceof Date) {
                            return target.cast(df.format((Date) source));
                        }
                        return null;
                    }
                };
            }
        }

        // this should handle java.util.Calendar to
        // String
        if (Calendar.class.isAssignableFrom(source)) {

            // target is string
            if (String.class.equals(target)) {
                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(TimeZone.getTimeZone("UTC")); // we DO work only with UTC times

                return new Converter() {
                    @Override
                    public <T> T convert(Object source, Class<T> target) throws Exception {
                        if (source instanceof Calendar) {
                            return target.cast(df.format(((Calendar) source).getTime()));
                        }
                        return null;
                    }
                };
            }
        }

        if (XMLGregorianCalendar.class.isAssignableFrom(source)) {
            // target is string
            if (String.class.equals(target)) {
                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(TimeZone.getTimeZone("UTC")); // we DO work only with UTC times

                return new Converter() {
                    @Override
                    public <T> T convert(Object source, Class<T> target) throws Exception {
                        if (source instanceof XMLGregorianCalendar) {
                            XMLGregorianCalendar xmlc = (XMLGregorianCalendar) source;
                            Date date = xmlc.toGregorianCalendar(GMT, Locale.getDefault(), null)
                                    .getTime();
                            return target.cast(df.format(date));
                        }
                        return null;
                    }
                };
            }
        }
        return null;
    }
}
