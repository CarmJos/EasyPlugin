package cc.carm.lib.easyplugin.database;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.carm.lib.easysql.api.builder.TableQueryBuilder;
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

	public DatabaseTable(@NotNull String tableName, @NotNull String[] columns, @Nullable String tableSettings) {
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
		return sqlManager.createQuery().inTable(tableName);
	}

}
