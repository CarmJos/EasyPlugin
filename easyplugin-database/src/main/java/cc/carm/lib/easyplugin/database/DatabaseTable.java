package cc.carm.lib.easyplugin.database;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.action.PreparedSQLUpdateAction;
import cc.carm.lib.easysql.api.action.PreparedSQLUpdateBatchAction;
import cc.carm.lib.easysql.api.builder.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class DatabaseTable {

	private final @NotNull String tableName;
	private final @NotNull String[] columns;

	@Nullable String tableSettings;

	public DatabaseTable(@NotNull String tableName, @NotNull String[] columns) {
		this(tableName, columns, null);
	}

	public DatabaseTable(@NotNull String tableName, @NotNull String[] columns,
						 @Nullable String tableSettings) {
		this.tableName = tableName;
		this.columns = columns;
		this.tableSettings = tableSettings;
	}

	public @NotNull String getTableName() {
		return tableName;
	}

	public @NotNull String[] getColumns() {
		return columns;
	}

	public @Nullable String getTableSettings() {
		return tableSettings;
	}

	public int createTable(SQLManager sqlManager) throws SQLException {
		TableCreateBuilder createAction = sqlManager.createTable(getTableName());
		createAction.setColumns(getColumns());
		if (getTableSettings() != null) createAction.setTableSettings(getTableSettings());
		return createAction.build().execute();
	}

	public TableQueryBuilder createQuery(SQLManager sqlManager) {
		return sqlManager.createQuery().inTable(getTableName());
	}

	public DeleteBuilder createDelete(SQLManager sqlManager) {
		return sqlManager.createDelete(getTableName());
	}

	public UpdateBuilder createUpdate(SQLManager sqlManager) {
		return sqlManager.createUpdate(getTableName());
	}

	public InsertBuilder<PreparedSQLUpdateAction> createInsert(SQLManager sqlManager) {
		return sqlManager.createInsert(getTableName());
	}

	public InsertBuilder<PreparedSQLUpdateBatchAction> createInsertBatch(SQLManager sqlManager) {
		return sqlManager.createInsertBatch(getTableName());
	}

	public ReplaceBuilder<PreparedSQLUpdateAction> createReplace(SQLManager sqlManager) {
		return sqlManager.createReplace(getTableName());
	}

	public ReplaceBuilder<PreparedSQLUpdateBatchAction> createReplaceBatch(SQLManager sqlManager) {
		return sqlManager.createReplaceBatch(getTableName());
	}

}
