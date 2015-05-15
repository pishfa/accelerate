package co.pishfa.accelerate.persistence;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

import java.sql.SQLException;

public class EclipseLinkSessionCustomizer implements SessionCustomizer {
	@Override
	public void customize(Session session) throws SQLException {
		// session.getLogin().useExternalConnectionPooling();
		for (ClassDescriptor descriptor : session.getDescriptors().values()) {
			for (DatabaseMapping mapping : descriptor.getMappings()) {
				if (mapping instanceof AggregateObjectMapping) {
					AggregateObjectMapping aggregateMapping = (AggregateObjectMapping) mapping;
					ClassDescriptor aggregateDesc = session
							.getDescriptor(aggregateMapping.getReferenceClass());
					for (DatabaseMapping aggregateFieldMapping : aggregateDesc
							.getMappings()) {
						if (aggregateFieldMapping.getField() != null) {
							DatabaseField dbField = new DatabaseField(
									(mapping.getAttributeName() + "_" + aggregateFieldMapping
											.getAttributeName()).toUpperCase());
							aggregateMapping.addFieldTranslation(dbField,
									aggregateFieldMapping.getAttributeName()
											.toUpperCase());
						}
					}
				}
			}

			/*
			 * if (!descriptor.getTables().isEmpty() &&
			 * descriptor.getAlias().equalsIgnoreCase
			 * (descriptor.getTableName())) { String tableName =
			 * descriptor.getTableName() + "_TABLE";
			 * descriptor.setTableName(tableName); for (IndexDefinition index :
			 * descriptor.getTables().get(0).getIndexes()) {
			 * index.setTargetTable(tableName); } }
			 */
		}
	}
}