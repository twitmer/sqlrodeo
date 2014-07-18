package sqlrodeo.implementation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sqlrodeo.ExecutionContext;

public final class ExecutionContextImplementation implements ExecutionContext {

	/** Delegate for Map<> implementation. */
	private final Map<String, Object> delegateMap = new HashMap<>();

	/** Service for implementing JEXL functionality. */
	private final JexlService jexlService = new JexlService();

	/** Logger */
	private final Logger log = LoggerFactory
			.getLogger(ExecutionContextImplementation.class);

	/**
	 * Constructor that initializes the context with the contents of
	 * System.getProperties() and System.getEnv().
	 */
	public ExecutionContextImplementation() {
		delegateMap.put("sysProps", System.getProperties());
		delegateMap.put("env", System.getenv());
	}

	@Override
	public void clear() {
		delegateMap.clear();
	}

	/**
	 * Close this context.
	 */
	@Override
	public void close() {
		if (log.isDebugEnabled()) {
			log.debug("close()");
		}

		// Close all connections.
		Iterator<Entry<String, Object>> iter = delegateMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = (Entry<String, Object>) iter.next();
			if (entry.getValue() instanceof Connection) {
				Connection conn = (Connection) entry.getValue();
				log.info("Closing connection: " + entry.getKey());
				try {
					conn.close();
				} catch (SQLException e) {
					log.info("Ignoring error when closing connection: "
							+ entry.getKey() + " : " + e.getMessage());
				}
			}
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return delegateMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return delegateMap.containsValue(value);
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return delegateMap.entrySet();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExecutionContextImplementation other = (ExecutionContextImplementation) obj;
		if (delegateMap == null) {
			if (other.delegateMap != null)
				return false;
		} else if (!delegateMap.equals(other.delegateMap))
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		return true;
	}

	@Override
	public Object evaluate(String jexlExpression)
			throws JexlEvaluationException {
		return jexlService.evaluate(jexlExpression, this);
	}

	@Override
	public Object evaluateScript(String jexlExpression)
			throws JexlEvaluationException {
		return jexlService.script(jexlExpression, this);
	}

	@Override
	public boolean evaluateBoolean(String jexlExpression)
			throws JexlEvaluationException {
		return jexlService.evaluateBoolean(jexlExpression, this);
	}

	@Override
	public Object get(Object key) {
		Object value = delegateMap.get(key);
		log.debug("get: " + key + " = " + value + " ("
				+ (value != null ? value.getClass().getName() : "null") + ")");
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((delegateMap == null) ? 0 : delegateMap.hashCode());
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		return result;
	}

	@Override
	public boolean isEmpty() {
		return delegateMap.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return delegateMap.keySet();
	}

	@Override
	public Object put(String key, Object value) {
		log.info("put: " + key + " = " + value + " ("
				+ (value != null ? value.getClass().getName() : "null") + ")");
		return delegateMap.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		for (Map.Entry<? extends String, ? extends Object> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Object remove(Object key) {
		return delegateMap.remove(key);
	}

	@Override
	public int size() {
		return delegateMap.size();
	}

	@Override
	public String substitute(String source) throws JexlEvaluationException {
		return jexlService.substitute(source, this);
	}

	@Override
	public Collection<Object> values() {
		return delegateMap.values();
	}
}
