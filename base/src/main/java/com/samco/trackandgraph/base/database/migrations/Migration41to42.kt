/*
 *  This file is part of Track & Graph
 *
 *  Track & Graph is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Track & Graph is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Track & Graph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.samco.trackandgraph.base.database.migrations


import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_41_42 = object : Migration(41, 42) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `time_histograms_table` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `graph_stat_id` INTEGER NOT NULL,
                `feature_id` INTEGER NOT NULL, 
                `duration` TEXT, 
                `window` INTEGER NOT NULL, 
                `sum_by_count` INTEGER NOT NULL, 
                `end_date` TEXT, 
                FOREIGN KEY(`graph_stat_id`) REFERENCES `graphs_and_stats_table2`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , 
                FOREIGN KEY(`feature_id`) REFERENCES `features_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
            )
            """.trimMargin()
        )
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_time_histograms_table_id` ON `time_histograms_table` (`id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_time_histograms_table_graph_stat_id` ON `time_histograms_table` (`graph_stat_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_time_histograms_table_feature_id` ON `time_histograms_table` (`feature_id`)")
    }
}
