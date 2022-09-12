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

package com.samco.trackandgraph.base.model

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SupportSQLiteQuery
import com.samco.trackandgraph.base.database.dto.*
import com.samco.trackandgraph.base.database.sampling.DataSample
import kotlinx.coroutines.flow.SharedFlow
import org.threeten.bp.Duration
import org.threeten.bp.OffsetDateTime
import java.io.InputStream
import java.io.OutputStream

//TODO This is too monolithic right now, needs to be split into multiple repository classes ideally.

//TODO for legacy reasons this class still contains some direct proxies to the database. This code should
// be abstracted away over time
interface DataInteractor : TrackerUpdater {
    @Deprecated(message = "Create a function that performs the interaction for you in the model implementation")
    fun doRawQuery(supportSQLiteQuery: SupportSQLiteQuery): Int

    fun getDatabaseFilePath(): String?

    fun closeOpenHelper()

    suspend fun insertGroup(group: Group): Long

    suspend fun deleteGroup(id: Long)

    suspend fun updateGroup(group: Group)

    fun getAllReminders(): LiveData<List<Reminder>>

    suspend fun getAllRemindersSync(): List<Reminder>

    fun getAllGroups(): LiveData<List<Group>>

    suspend fun getAllGroupsSync(): List<Group>

    suspend fun getAllTrackersSync(): List<Tracker>

    suspend fun updateReminders(reminders: List<Reminder>)

    suspend fun getGroupById(id: Long): Group

    suspend fun updateGroupChildOrder(groupId: Long, children: List<GroupChild>)

    suspend fun getDisplayTrackersForGroupSync(groupId: Long): List<DisplayTracker>

    suspend fun getFeaturesForGroupSync(groupId: Long): List<Feature>

    suspend fun getTrackerById(trackerId: Long): Tracker?

    suspend fun getFeatureById(featureId: Long): Feature?

    suspend fun tryGetDisplayTrackerByIdSync(trackerId: Long): DisplayTracker?

    suspend fun getTrackersByIdsSync(trackerIds: List<Long>): List<Tracker>

    suspend fun insertTracker(tracker: Tracker): Long

    //TODO implement the interface changes and get base compiling.
    //TODO make sure the migration tests pass
    //TODO fix up app layer. good luck!
    suspend fun updateTracker(tracker: Tracker)

    suspend fun deleteDataPoint(dataPoint: DataPoint)

    suspend fun deleteGraphOrStat(id: Long)

    suspend fun deleteGraphOrStat(graphOrStat: GraphOrStat)

    suspend fun deleteFeature(featureId: Long)

    suspend fun insertDataPoint(dataPoint: DataPoint): Long

    suspend fun insertDataPoints(dataPoint: List<DataPoint>)

    suspend fun updateDataPoints(dataPoint: List<DataPoint>)

    suspend fun getDataSampleForFeatureId(featureId: Long): DataSample

    suspend fun getLabelsForFeature(feature: Feature): Set<String>

    suspend fun getDataSampleForFeature(feature: Feature): DataSample

    /**
     * Emits a unit every time currently displayed data may have changed.
     * For example if you create/update/remove a data point.
     */
    //TODO function add/update/delete actions should trigger this
    fun getDataUpdateEvents(): SharedFlow<Unit>

    suspend fun getDataPointByTimestampAndTrackerSync(
        trackerId: Long,
        timestamp: OffsetDateTime
    ): DataPoint?

    suspend fun getGraphStatById(graphStatId: Long): GraphOrStat

    suspend fun tryGetGraphStatById(graphStatId: Long): GraphOrStat?

    suspend fun getLineGraphByGraphStatId(graphStatId: Long): LineGraphWithFeatures?

    suspend fun getPieChartByGraphStatId(graphStatId: Long): PieChart?

    suspend fun getAverageTimeBetweenStatByGraphStatId(graphStatId: Long): AverageTimeBetweenStat?

    suspend fun getTimeSinceLastStatByGraphStatId(graphStatId: Long): TimeSinceLastStat?

    suspend fun getGraphsAndStatsByGroupIdSync(groupId: Long): List<GraphOrStat>

    suspend fun getAllGraphStatsSync(): List<GraphOrStat>

    fun getAllDisplayNotes(): LiveData<List<DisplayNote>>

    //TODO rename this back once you've ensured it's used correctly everywhere
    suspend fun removeNote2(timestamp: OffsetDateTime, trackerId: Long)

    suspend fun deleteGlobalNote(note: GlobalNote)

    suspend fun insertGlobalNote(note: GlobalNote): Long

    suspend fun getGlobalNoteByTimeSync(timestamp: OffsetDateTime?): GlobalNote?

    suspend fun getAllGlobalNotesSync(): List<GlobalNote>

    suspend fun duplicateLineGraph(graphOrStat: GraphOrStat): Long?

    suspend fun duplicatePieChart(graphOrStat: GraphOrStat): Long?

    suspend fun duplicateAverageTimeBetweenStat(graphOrStat: GraphOrStat): Long?

    suspend fun duplicateTimeSinceLastStat(graphOrStat: GraphOrStat): Long?

    suspend fun duplicateTimeHistogram(graphOrStat: GraphOrStat): Long?

    suspend fun insertLineGraph(graphOrStat: GraphOrStat, lineGraph: LineGraphWithFeatures): Long

    suspend fun insertPieChart(graphOrStat: GraphOrStat, pieChart: PieChart): Long

    suspend fun insertAverageTimeBetweenStat(
        graphOrStat: GraphOrStat,
        averageTimeBetweenStat: AverageTimeBetweenStat
    ): Long

    suspend fun insertTimeSinceLastStat(
        graphOrStat: GraphOrStat,
        timeSinceLastStat: TimeSinceLastStat
    ): Long

    suspend fun insertTimeHistogram(graphOrStat: GraphOrStat, timeHistogram: TimeHistogram): Long

    suspend fun updatePieChart(graphOrStat: GraphOrStat, pieChart: PieChart)

    suspend fun updateAverageTimeBetweenStat(
        graphOrStat: GraphOrStat,
        averageTimeBetweenStat: AverageTimeBetweenStat
    )

    suspend fun updateLineGraph(graphOrStat: GraphOrStat, lineGraph: LineGraphWithFeatures)

    suspend fun updateTimeSinceLastStat(
        graphOrStat: GraphOrStat,
        timeSinceLastStat: TimeSinceLastStat
    )

    suspend fun updateGraphOrStat(graphOrStat: GraphOrStat)

    suspend fun updateTimeHistogram(graphOrStat: GraphOrStat, timeHistogram: TimeHistogram)

    suspend fun getTimeHistogramByGraphStatId(graphStatId: Long): TimeHistogram?

    suspend fun getGroupsForGroupSync(id: Long): List<Group>

    suspend fun writeFeaturesToCSV(outStream: OutputStream, featureIds: List<Long>)

    suspend fun readFeaturesFromCSV(inputStream: InputStream, trackGroupId: Long)

    suspend fun playTimerForTracker(trackerId: Long)

    suspend fun stopTimerForTracker(trackerId: Long): Duration?

    suspend fun getAllActiveTimerTrackers(): List<DisplayTracker>

    suspend fun getFunctionById(functionId: Long): FunctionDto?

    suspend fun updateFunction(function: FunctionDto)

    suspend fun createFunction(function: FunctionDto)
}