/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.api.geometry.complex;

import static org.geotools.api.annotation.Specification.ISO_19107;

import org.geotools.api.annotation.UML;
import org.geotools.api.geometry.Boundary;

/**
 * The boundary of {@linkplain Complex complex} objects. The {@link
 * org.geotools.api.geometry.Geometry#getBoundary getBoundary()} method for {@link Complex} objects
 * shall return a {@code ComplexBoundary}, which is a collection of primitives and a {@linkplain
 * Complex complex} of dimension 1 less than the original object.
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/as">ISO 19107</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 1.0
 */
@UML(identifier = "GM_ComplexBoundary", specification = ISO_19107)
public interface ComplexBoundary extends Boundary {}