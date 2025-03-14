package org.geotools.brewer.styling.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geotools.api.filter.PropertyIsGreaterThan;
import org.geotools.api.style.FeatureTypeConstraint;
import org.geotools.api.style.NamedLayer;
import org.geotools.api.style.NamedStyle;
import org.geotools.api.style.PointSymbolizer;
import org.geotools.api.style.StyledLayerDescriptor;
import org.geotools.api.style.UserLayer;
import org.junit.Test;

public class SLDTest extends AbstractStyleTest {

    @Test
    public void testSimpleNamed() {
        StyledLayerDescriptor sld = new NamedLayerBuilder()
                .name("states")
                .style()
                .featureTypeStyle()
                .rule()
                .point()
                .buildSLD();
        // print(sld);

        StyleCollector collector = new StyleCollector();
        sld.accept(collector);
        assertSimpleStyle(collector);

        NamedLayer layer = (NamedLayer) collector.layers.get(0);
        assertEquals("states", layer.getName());
        assertTrue(collector.symbolizers.get(0) instanceof PointSymbolizer);
    }

    @Test
    public void testNamedStyle() {
        StyledLayerDescriptor sld = new NamedLayerBuilder()
                .name("states")
                .style()
                .name("population")
                .buildSLD();
        // print(sld);

        StyleCollector collector = new StyleCollector();
        sld.accept(collector);
        assertEquals(0, collector.featureTypeStyles.size());
        assertEquals(0, collector.rules.size());
        assertEquals(0, collector.symbolizers.size());
        assertEquals(1, collector.styles.size());
        assertEquals(1, collector.layers.size());

        NamedLayer layer = (NamedLayer) collector.layers.get(0);
        assertEquals("states", layer.getName());
        NamedStyle ns = (NamedStyle) layer.getStyles()[0];
        assertEquals("population", ns.getName());
    }

    @Test
    public void testRemoteOWS() {
        PropertyIsGreaterThan tenMillionPeople = ff.greater(ff.property("PERSONS"), ff.literal(10000000));

        UserLayerBuilder lb = new UserLayerBuilder();
        lb.remoteOWS("http://geoserver.org/geoserver/ows", "WFS");
        lb.featureTypeConstraint().featureTypeName("states").filter(tenMillionPeople);
        lb.style().featureTypeStyle().rule().polygon().fill();
        StyledLayerDescriptor sld = lb.buildSLD();
        // print(sld);

        StyleCollector collector = new StyleCollector();
        sld.accept(collector);
        assertSimpleStyle(collector);

        UserLayer layer = (UserLayer) collector.layers.get(0);
        assertEquals("http://geoserver.org/geoserver/ows", layer.getRemoteOWS().getOnlineResource());
        assertEquals("WFS", layer.getRemoteOWS().getService());
        FeatureTypeConstraint constraint = layer.getLayerFeatureConstraints()[0];
        assertEquals("states", constraint.getFeatureTypeName());
        assertEquals(tenMillionPeople, constraint.getFilter());
    }
}
