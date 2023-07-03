/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.api.geometry.primitive;

import static org.geotools.api.annotation.Obligation.MANDATORY;
import static org.geotools.api.annotation.Obligation.OPTIONAL;
import static org.geotools.api.annotation.Specification.ISO_19107;

import org.geotools.api.annotation.UML;
import org.geotools.api.geometry.complex.CompositeCurve;

/**
 * A curve and an orientation inherited from {@link OrientablePrimitive}. If the orientation is
 * positive, then the {@code OrientableCurve} is a {@linkplain Curve curve}. If the orientation is
 * negative, then the {@code OrientableCurve} is related to another {@linkplain Curve curve} with a
 * parameterization that reverses the sense of the curve traversal.
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/as">ISO 19107</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 1.0
 */
@UML(identifier = "GM_OrientableCurve", specification = ISO_19107)
public interface OrientableCurve extends OrientablePrimitive {
    /**
     * Returns an ordered pair of points, which are the start point and end point of the curve. If
     * the curve is closed, then the boundary shall be empty.
     *
     * @return The sets of positions on the boundary.
     */
    @Override
    @UML(identifier = "boundary", obligation = MANDATORY, specification = ISO_19107)
    CurveBoundary getBoundary();

    /**
     * Returns the primitive associated with this {@code OrientableCurve}.
     *
     * @return The primitive, or {@code null} if the association is not available or not implemented
     *     that way.
     * @see Curve#getProxy
     * @issue http://jira.codehaus.org/browse/GEO-63
     */
    @Override
    @UML(identifier = "primitive", obligation = OPTIONAL, specification = ISO_19107)
    Curve getPrimitive();

    /**
     * Returns the owner of this orientable curve. This method is <em>optional</em> since the
     * association in ISO 19107 is navigable only from {@code CompositeCurve} to {@code
     * OrientableCurve}, not the other way.
     *
     * @return The owner of this orientable curve, or {@code null} if the association is not
     *     available or not implemented that way.
     * @see CompositeCurve#getGenerators
     * @issue http://jira.codehaus.org/browse/GEO-63
     */
    @Override
    @UML(identifier = "composite", obligation = OPTIONAL, specification = ISO_19107)
    CompositeCurve getComposite();
}