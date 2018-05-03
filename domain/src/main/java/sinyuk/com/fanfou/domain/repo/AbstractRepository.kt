package sinyuk.com.fanfou.domain.repo

import sinyuk.com.fanfou.domain.api.RestAPI
import javax.inject.Inject


@Suppress("unused")
abstract class AbstractRepository constructor(private val restAPI: RestAPI)
