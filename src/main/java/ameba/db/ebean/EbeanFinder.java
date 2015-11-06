package ameba.db.ebean;

import ameba.db.model.Finder;
import com.avaje.ebean.*;
import com.avaje.ebean.text.PathProperties;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base-class for model-mapped models that provides convenience methods.
 *
 * @author sulijuan
 */
public class EbeanFinder<ID, T> extends Finder<ID, T> {

    private EbeanServer server;

    private Query<T> query;

    /**
     * <p>Constructor for EbeanFinder.</p>
     *
     * @param serverName a {@link java.lang.String} object.
     * @param idType     a {@link java.lang.Class} object.
     * @param type       a {@link java.lang.Class} object.
     */
    public EbeanFinder(String serverName, Class<ID> idType, Class<T> type) {
        super(serverName, idType, type);
        server = Ebean.getServer(getServerName());
    }

    /**
     * <p>query.</p>
     *
     * @return a {@link com.avaje.ebean.Query} object.
     */
    public Query<T> query() {
        if (query == null) {
            query = createQuery();
        }
        return query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <M extends T> Finder<ID, M> on(String server) {
        return new EbeanFinder(server, getIdType(), getModelType());
    }

    private EbeanServer server() {
        return server;
    }

    /**
     * Retrieves an entity by ID.
     *
     * @param id a ID object.
     * @return a M object.
     */
    @SuppressWarnings("unchecked")
    public <M extends T> M byId(ID id) {
        return (M) server().find(getModelType(), id);
    }

    /**
     * Retrieves an entity reference for this ID.
     *
     * @param id a ID object.
     * @return a M object.
     */
    @SuppressWarnings("unchecked")
    public <M extends T> M ref(ID id) {
        return (M) server().getReference(getModelType(), id);
    }

    /**
     * Creates a filter for sorting and filtering lists of entities locally without going back to the database.
     *
     * @return a {@link com.avaje.ebean.Filter} object.
     */
    public Filter<T> filter() {
        return server().filter(getModelType());
    }

    /**
     * Creates a query.
     *
     * @return a {@link com.avaje.ebean.Query} object.
     * @since 0.1.6e
     */
    public Query<T> createQuery() {
        return server().createQuery(getModelType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> createNamedQuery(String name) {
        return server().createNamedQuery(getModelType(), name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlQuery createNamedSqlQuery(String name) {
        return server().createNamedSqlQuery(name);
    }

    /**
     * {@inheritDoc}
     */
    public SqlQuery createSqlQuery(String sql) {
        return server().createSqlQuery(sql);
    }

    /**
     * Returns the next identity value.
     *
     * @return a I object.
     */
    @SuppressWarnings("unchecked")
    public <I extends ID> I nextId() {
        return (I) server().nextId(getModelType());
    }


    /**
     * {@inheritDoc}
     */
    public Query<T> setPersistenceContextScope(PersistenceContextScope persistenceContextScope) {
        return query().setPersistenceContextScope(persistenceContextScope);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Specifies a path to load including all its properties.
     */
    public Query<T> fetch(String path) {
        return query().fetch(path);
    }

    /**
     * Additionally specifies a <code>JoinConfig</code> to specify a 'query join' and/or define the lazy loading query.
     *
     * @param path       a {@link java.lang.String} object.
     * @param joinConfig a {@link com.avaje.ebean.FetchConfig} object.
     * @return a {@link com.avaje.ebean.Query} object.
     */
    public Query<T> fetch(String path, FetchConfig joinConfig) {
        return query().fetch(path, joinConfig);
    }


    /**
     * {@inheritDoc}
     */
    public Query<T> apply(PathProperties pathProperties) {
        return query().apply(pathProperties);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Specifies a path to fetch with a specific list properties to include, to load a partial object.
     */
    public Query<T> fetch(String path, String fetchProperties) {
        return query().fetch(path, fetchProperties);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Additionally specifies a <code>FetchConfig</code> to use a separate query or lazy loading to load this path.
     */
    public Query<T> fetch(String assocProperty, String fetchProperties, FetchConfig fetchConfig) {
        return query().fetch(assocProperty, fetchProperties, fetchConfig);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Applies a filter on the 'many' property list rather than the root level objects.
     */
    public ExpressionList<T> filterMany(String propertyName) {
        return query().filterMany(propertyName);
    }

    /**
     * Executes a find IDs query in a background thread.
     *
     * @return a {@link com.avaje.ebean.FutureIds} object.
     */
    public FutureIds<T> findFutureIds() {
        return query().findFutureIds();
    }

    /**
     * Executes a find list query in a background thread.
     *
     * @return a {@link com.avaje.ebean.FutureList} object.
     */
    public FutureList<T> findFutureList() {
        return query().findFutureList();
    }

    /**
     * Executes a find row count query in a background thread.
     *
     * @return a {@link com.avaje.ebean.FutureRowCount} object.
     */
    public FutureRowCount<T> findFutureRowCount() {
        return query().findFutureRowCount();
    }

    /**
     * Executes a query and returns the results as a list of IDs.
     *
     * @return a {@link java.util.List} object.
     */
    public List<Object> findIds() {
        return query().findIds();
    }

    /**
     * Executes the query and returns the results as a list of objects.
     *
     * @return a {@link java.util.List} object.
     */
    @SuppressWarnings("unchecked")
    public <M extends T> List<M> findList() {
        return (List<M>) query().findList();
    }

    /**
     * Executes the query and returns the results as a map of objects.
     *
     * @return a {@link java.util.Map} object.
     */
    @SuppressWarnings("unchecked")
    public <M extends T> Map<?, M> findMap() {
        return (Map<?, M>) query().findMap();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Executes the query and returns the results as a map of the objects.
     */
    @SuppressWarnings("unchecked")
    public <K, M extends T> Map<K, M> findMap(String a, Class<K> b) {
        return (Map<K, M>) query().findMap(a, b);
    }


    /**
     * {@inheritDoc}
     */
    public PagedList<T> findPagedList(int i, int i2) {
        return query().findPagedList(i, i2);
    }

    /**
     * Returns the number of entities this query should return.
     *
     * @return a int.
     */
    public int findRowCount() {
        return query().findRowCount();
    }

    /**
     * Executes the query and returns the results as a set of objects.
     *
     * @return a {@link java.util.Set} object.
     */
    @SuppressWarnings("unchecked")
    public <M extends T> Set<M> findSet() {
        return (Set<M>) query().findSet();
    }

    /**
     * Executes the query and returns the results as either a single bean or <code>null</code>, if no matching bean is found.
     *
     * @return a M object.
     */
    @SuppressWarnings("unchecked")
    public <M extends T> M findUnique() {
        return (M) query().findUnique();
    }

    /**
     * {@inheritDoc}
     */
    public void findEach(QueryEachConsumer<T> consumer) {
        query().findEach(consumer);
    }

    /**
     * {@inheritDoc}
     */
    public void findEachWhile(QueryEachWhileConsumer<T> consumer) {
        query().findEachWhile(consumer);
    }

    /**
     * <p>findIterate.</p>
     *
     * @return a {@link com.avaje.ebean.QueryIterator} object.
     */
    public QueryIterator<T> findIterate() {
        return query().findIterate();
    }


    @Override
    public List<Version<T>> findVersions() {
        return query().findVersions();
    }

    @Override
    public List<Version<T>> findVersionsBetween(Timestamp start, Timestamp end) {
        return query().findVersionsBetween(start, end);
    }

    @Override
    public int delete() {
        return query().delete();
    }

    /**
     * Returns the <code>ExpressionFactory</code> used by this query.
     *
     * @return a {@link com.avaje.ebean.ExpressionFactory} object.
     */
    public ExpressionFactory getExpressionFactory() {
        return query().getExpressionFactory();
    }

    @Override
    public boolean isAutoTuned() {
        return query().isAutoTuned();
    }

    /**
     * Returns the first row value.
     *
     * @return a int.
     */
    public int getFirstRow() {
        return query().getFirstRow();
    }

    /**
     * Returns the SQL that was generated for executing this query.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGeneratedSql() {
        return query().getGeneratedSql();
    }

    /**
     * Returns the maximum of rows for this query.
     *
     * @return a int.
     */
    public int getMaxRows() {
        return query().getMaxRows();
    }

    /**
     * Returns the query's <code>having</code> clause.
     *
     * @return a {@link com.avaje.ebean.ExpressionList} object.
     */
    public ExpressionList<T> having() {
        return query().having();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Adds an expression to the <code>having</code> clause and returns the query.
     */
    public Query<T> having(com.avaje.ebean.Expression addExpressionToHaving) {
        return query().having(addExpressionToHaving);
    }

    /**
     * Adds clauses to the <code>having</code> clause and returns the query.
     *
     * @param addToHavingClause a {@link java.lang.String} object.
     * @return a {@link com.avaje.ebean.Query} object.
     */
    public Query<T> having(String addToHavingClause) {
        return query().having(addToHavingClause);
    }

    /**
     * Returns the <code>order by</code> clause so that you can append an ascending or descending property to the <code>order by</code> clause.
     * This is exactly the same as {@link #orderBy}.
     *
     * @return OrderBy
     */
    public OrderBy<T> order() {
        return query().order();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets the <code>order by</code> clause, replacing the existing <code>order by</code> clause if there is one.
     * This is exactly the same as {@link #orderBy(String)}.
     */
    public Query<T> order(String orderByClause) {
        return query().order(orderByClause);
    }

    /**
     * Returns the <code>order by</code> clause so that you can append an ascending or descending property to the <code>order by</code> clause.
     * This is exactly the same as {@link #order}.
     *
     * @return a {@link com.avaje.ebean.OrderBy} object.
     */
    public OrderBy<T> orderBy() {
        return query().orderBy();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Set the <code>order by</code> clause replacing the existing <code>order by</code> clause if there is one.
     * <p/>
     * This is exactly the same as {@link #order(String)}.
     */
    public Query<T> orderBy(String orderByClause) {
        return query().orderBy(orderByClause);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Explicitly sets a comma delimited list of the properties to fetch on the 'main' entity bean, to load a partial object.
     */
    public Query<T> select(String fetchProperties) {
        return query().select(fetchProperties);
    }

    /**
     * {@inheritDoc}
     */
    public Query<T> setAutoTune(boolean autoTune) {
        return query().setAutoTune(autoTune);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Set the default lazy loading batch size to use.
     * <p/>
     * When lazy loading is invoked on beans loaded by this query then this sets the
     * batch size used to load those beans.
     */
    public Query<T> setLazyLoadBatchSize(int lazyLoadBatchSize) {
        return query().setLazyLoadBatchSize(lazyLoadBatchSize);
    }

    @Override
    public Query<T> setDisableReadAuditing() {
        return query().setDisableReadAuditing();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets a hint, which for JDBC translates to <code>Statement.fetchSize()</code>.
     */
    public Query<T> setBufferFetchSizeHint(int fetchSize) {
        return query().setBufferFetchSizeHint(fetchSize);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets whether this query uses <code>DISTINCT</code>.
     */
    public Query<T> setDistinct(boolean isDistinct) {
        return query().setDistinct(isDistinct);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets the first row to return for this query.
     */
    public Query<T> setFirstRow(int firstRow) {
        return query().setFirstRow(firstRow);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets the ID value to query.
     */
    public Query<T> setId(Object id) {
        return query().setId(id);
    }

    public Object getId() {
        return query().getId();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * When set to <code>true</code>, all the beans from this query are loaded into the bean cache.
     */
    public Query<T> setLoadBeanCache(boolean loadBeanCache) {
        return query().setLoadBeanCache(loadBeanCache);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets the property to use as keys for a map.
     */
    public Query<T> setMapKey(String mapKey) {
        return query().setMapKey(mapKey);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets the maximum number of rows to return in the query.
     */
    public Query<T> setMaxRows(int maxRows) {
        return query().setMaxRows(maxRows);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Replaces any existing <code>order by</code> clause using an <code>OrderBy</code> object.
     * <p/>
     * This is exactly the same as {@link #setOrderBy(com.avaje.ebean.OrderBy)}.
     */
    public Query<T> setOrder(OrderBy<T> orderBy) {
        return query().setOrder(orderBy);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Set an OrderBy object to replace any existing <code>order by</code> clause.
     * <p/>
     * This is exactly the same as {@link #setOrder(com.avaje.ebean.OrderBy)}.
     */
    public Query<T> setOrderBy(OrderBy<T> orderBy) {
        return query().setOrderBy(orderBy);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets an ordered bind parameter according to its position.
     */
    public Query<T> setParameter(int position, Object value) {
        return query().setParameter(position, value);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets a named bind parameter.
     */
    public Query<T> setParameter(String name, Object value) {
        return query().setParameter(name, value);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets the OQL query to run
     */
    public Query<T> setQuery(String oql) {
        return (query = server().createQuery(getModelType(), oql));
    }


    /**
     * {@inheritDoc}
     * <p/>
     * Sets <code>RawSql</code> to use for this query.
     */
    public Query<T> setRawSql(RawSql rawSql) {
        return query().setRawSql(rawSql);
    }

    @Override
    public RawSql getRawSql() {
        return query().getRawSql();
    }

    @Override
    public Query<T> asOf(Timestamp asOf) {
        return query().asOf(asOf);
    }

    @Override
    public void cancel() {
        query().cancel();
    }

    @Override
    public Query<T> copyQuery() {
        return query().copy();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets whether the returned beans will be read-only.
     */
    public Query<T> setReadOnly(boolean readOnly) {
        return query().setReadOnly(readOnly);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets a timeout on this query.
     */
    public Query<T> setTimeout(int secs) {
        return query().setTimeout(secs);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets whether to use the bean cache.
     */
    public Query<T> setUseCache(boolean useBeanCache) {
        return query().setUseCache(useBeanCache);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Sets whether to use the query cache.
     */
    public Query<T> setUseQueryCache(boolean useQueryCache) {
        return query().setUseQueryCache(useQueryCache);
    }

    /**
     * Adds expressions to the <code>where</code> clause with the ability to chain on the <code>ExpressionList</code>.
     *
     * @return a {@link com.avaje.ebean.ExpressionList} object.
     */
    public ExpressionList<T> where() {
        return query().where();
    }

    /**
     * Adds a single <code>Expression</code> to the <code>where</code> clause and returns the query.
     *
     * @param expression a {@link com.avaje.ebean.Expression} object.
     * @return a {@link com.avaje.ebean.Query} object.
     */
    public Query<T> where(com.avaje.ebean.Expression expression) {
        return query().where(expression);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Adds additional clauses to the <code>where</code> clause.
     */
    public Query<T> where(String addToWhereClause) {
        return query().where(addToWhereClause);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Execute the select with "for update" which should lock the record "on read"
     */
    public Query<T> setForUpdate(boolean forUpdate) {
        return query().setForUpdate(forUpdate);
    }

    @Override
    public boolean isForUpdate() {
        return query().isForUpdate();
    }

    @Override
    public Query<T> alias(String alias) {
        return query().alias(alias);
    }

    @Override
    public Class<T> getBeanType() {
        return query.getBeanType();
    }

    @Override
    public Query<T> setDisableLazyLoading(boolean disableLazyLoading) {
        return query().setDisableLazyLoading(disableLazyLoading);
    }
}
