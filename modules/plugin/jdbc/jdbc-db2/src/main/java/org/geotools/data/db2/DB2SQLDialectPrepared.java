/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.db2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.PreparedFilterToSQL;
import org.geotools.jdbc.PreparedStatementSQLDialect;
import org.geotools.util.factory.Hints;
import org.geotools.util.factory.Hints.Key;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class DB2SQLDialectPrepared extends PreparedStatementSQLDialect {

    private DB2SQLDialect delegate = null;

    public DB2SQLDialectPrepared(JDBCDataStore dataStore, DB2DialectInfo info) {
        super(dataStore);
        delegate = new DB2SQLDialect(dataStore, info);
    }

    /* (non-Javadoc)
     * @see org.geotools.jdbc.SQLDialect#createCRS(int, java.sql.Connection)
     *
     */
    @Override
    public CoordinateReferenceSystem createCRS(int srid, Connection cx) throws SQLException {
        return delegate.createCRS(srid, cx);
    }

    @Override
    public PreparedFilterToSQL createPreparedFilterToSQL() {
        DB2FilterToSQL filter = new DB2FilterToSQL(this);
        filter.setFunctionEncodingEnabled(isFunctionEncodingEnabled());
        filter.setLooseBBOXEnabled(delegate.isLooseBBOXEnabled());
        if (delegate.isUseSelectivity()) filter.setSelectivityClause(DB2SQLDialect.SELECTIVITY_CLAUSE);
        return filter;
    }

    public boolean isLooseBBOXEnabled() {
        return delegate.isLooseBBOXEnabled();
    }

    public void setLooseBBOXEnabled(boolean looseBBOXEnabled) {
        delegate.setLooseBBOXEnabled(looseBBOXEnabled);
    }

    @Override
    public void encodePrimaryKey(String column, StringBuffer sql) {
        delegate.encodePrimaryKey(column, sql);
    }

    @Override
    public String getGeometryTypeName(Integer type) {
        return delegate.getGeometryTypeName(type);
    }

    @Override
    public Integer getGeometrySRID(String schemaName, String tableName, String columnName, Connection cx)
            throws SQLException {
        return delegate.getGeometrySRID(schemaName, tableName, columnName, cx);
    }

    public void encodeGeometryColumn(GeometryDescriptor gatt, StringBuffer sql) {
        delegate.encodeGeometryColumn(gatt, null, sql);
    }

    @Override
    public void encodeGeometryColumn(GeometryDescriptor gatt, String prefix, int srid, Hints hints, StringBuffer sql) {
        delegate.encodeGeometryColumn(gatt, prefix, srid, hints, sql);
    }

    @Override
    public void encodeGeometryEnvelope(String tableName, String geometryColumn, StringBuffer sql) {
        delegate.encodeGeometryEnvelope(tableName, geometryColumn, sql);
    }

    @Override
    public Envelope decodeGeometryEnvelope(ResultSet rs, int column, Connection cx) throws SQLException, IOException {
        return delegate.decodeGeometryEnvelope(rs, column, cx);
    }

    @Override
    public Geometry decodeGeometryValue(
            GeometryDescriptor descriptor,
            ResultSet rs,
            String name,
            GeometryFactory factory,
            Connection cx,
            Hints hints)
            throws IOException, SQLException {
        return delegate.decodeGeometryValue(descriptor, rs, name, factory, cx, hints);
    }

    @Override
    public Geometry decodeGeometryValue(
            GeometryDescriptor descriptor,
            ResultSet rs,
            int column,
            GeometryFactory factory,
            Connection cx,
            Hints hints)
            throws IOException, SQLException {
        return delegate.decodeGeometryValue(descriptor, rs, column, factory, cx, new Hints());
    }

    @Override
    public void registerClassToSqlMappings(Map<Class<?>, Integer> mappings) {
        delegate.registerClassToSqlMappings(mappings);
    }

    @Override
    public void registerSqlTypeToClassMappings(Map<Integer, Class<?>> mappings) {
        delegate.registerSqlTypeToClassMappings(mappings);
    }

    @Override
    public void registerSqlTypeNameToClassMappings(Map<String, Class<?>> mappings) {
        delegate.registerSqlTypeNameToClassMappings(mappings);
    }

    @Override
    public void prepareFunctionArgument(Class clazz, StringBuffer sql) {
        String castExpression = DB2Util.getCastExpression(clazz);

        if (castExpression != null) sql.append(castExpression);
        else super.prepareFunctionArgument(clazz, sql);
    }

    @Override
    public void postCreateTable(String schemaName, SimpleFeatureType featureType, Connection cx) throws SQLException {
        delegate.postCreateTable(schemaName, featureType, cx);
    }

    @Override
    public void setGeometryValue(Geometry g, int dimension, int srid, Class binding, PreparedStatement ps, int column)
            throws SQLException {
        if (g == null || g.isEmpty()) {
            // ps.setNull(column, Types.OTHER);
            ps.setBytes(column, null);
            return;
        }
        DB2WKBWriter w = new DB2WKBWriter(dimension, getDb2DialectInfo().isHasOGCWkbZTyps());
        byte[] bytes = w.write(g);
        ps.setBytes(column, bytes);
    }

    @Override
    public String getSequenceForColumn(String schemaName, String tableName, String columnName, Connection cx)
            throws SQLException {

        return delegate.getSequenceForColumn(schemaName, tableName, columnName, cx);
    }

    @Override
    public Object getNextSequenceValue(String schemaName, String sequenceName, Connection cx) throws SQLException {
        return delegate.getNextSequenceValue(schemaName, sequenceName, cx);
    }

    @Override
    public String encodeNextSequenceValue(String schemaName, String sequenceName) {
        return delegate.encodeNextSequenceValue(schemaName, sequenceName);
    }

    @Override
    public boolean lookupGeneratedValuesPostInsert() {
        return delegate.lookupGeneratedValuesPostInsert();
    }

    @Override
    public Object getLastAutoGeneratedValue(String schemaName, String tableName, String columnName, Connection cx)
            throws SQLException {
        return delegate.getLastAutoGeneratedValue(schemaName, tableName, columnName, cx);
    }

    @Override
    public void prepareGeometryValue(
            Class<? extends Geometry> gClass, int dimension, int srid, Class binding, StringBuffer sql) {
        DB2Util.prepareGeometryValue(gClass, srid, binding, sql);
    }

    @Override
    public boolean isLimitOffsetSupported() {
        return delegate.isLimitOffsetSupported();
    }

    @Override
    public void applyLimitOffset(StringBuffer sql, int limit, int offset) {
        delegate.applyLimitOffset(sql, limit, offset);
    }

    @Override
    public void encodeGeometryColumnGeneralized(
            GeometryDescriptor gatt, String prefix, int srid, StringBuffer sql, Double distance) {

        delegate.encodeGeometryColumnGeneralized(gatt, prefix, srid, sql, distance);
    }

    @Override
    protected void addSupportedHints(Set<Key> hints) {
        delegate.addSupportedHints(hints);
    }

    @Override
    public boolean includeTable(String schemaName, String tableName, Connection cx) throws SQLException {
        return delegate.includeTable(schemaName, tableName, cx);
    }

    public DB2DialectInfo getDb2DialectInfo() {
        return delegate.getDb2DialectInfo();
    }

    public boolean isFunctionEncodingEnabled() {
        return delegate.isFunctionEncodingEnabled();
    }

    public void setFunctionEncodingEnabled(boolean functionEncodingEnabled) {
        delegate.setFunctionEncodingEnabled(functionEncodingEnabled);
    }

    @Override
    public List<ReferencedEnvelope> getOptimizedBounds(String schema, SimpleFeatureType featureType, Connection cx)
            throws SQLException, IOException {
        return delegate.getOptimizedBounds(schema, featureType, cx);
    }

    public boolean isUseSelectivity() {
        return delegate.isUseSelectivity();
    }

    public void setUseSelectivity(boolean useSelectivity) {
        delegate.setUseSelectivity(useSelectivity);
    }

    @Override
    protected boolean supportsSchemaForIndex() {
        return delegate.supportsSchemaForIndex();
    }

    @Override
    public boolean canGroupOnGeometry() {
        return delegate.canGroupOnGeometry();
    }
}
