
package ro.code4.casefile.sync

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import ro.code4.casefile.repositories.Repository
import ro.code4.casefile.services.ApiInterface

class CitiesAndCountiesWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams), KoinComponent {

    private val api: ApiInterface by inject()
    private val repository: Repository by inject()

    override fun doWork(): Result {

        var result = Result.success()
        api.getCounties().take(1)
            .subscribeOn(Schedulers.io())
            .blockingSubscribe({ counties ->
                repository.saveCounties(counties)
                counties.map { county ->
                    api.getCities(county.countyId)
                        .blockingSubscribe( { cities ->
                            cities.map { city ->
                                city.countyId = county.countyId
                            }
                            repository.saveCities(cities)
                        }, {
                            result = Result.retry()
                        })
                }
            }, {
                result = Result.retry()
            })

        // Indicate whether the work finished successfully with the Result
        return result
    }
}
