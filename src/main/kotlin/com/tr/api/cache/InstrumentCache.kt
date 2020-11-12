package com.tr.api.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutionException
import javax.annotation.PostConstruct

@Service
class InstrumentCache {

    companion object {
        private val logger = LoggerFactory.getLogger(InstrumentCache::class.java)
    }

    private lateinit var cache: LoadingCache<String, Double>

    @PostConstruct
    fun setUp() {
        cache = CacheBuilder.newBuilder()
                .build(object : CacheLoader<String, Double>() {
                    override fun load(key: String): Double? {
                        return null
                    }
                })
    }

    fun getInstrumentPrice(isin: String): Double {
        try {
            val value = cache.get(isin)
            value ?: throw RuntimeException("No instrument found with isin: $isin")
            return value
        } catch (e: ExecutionException) {
            logger.error("error trying to extract instrument price from cache with isin: $isin")
            throw e
        }
    }

    fun updatePrice(isin: String, price: Double) {
        try {
            cache.put(isin, price)
        } catch (e: ExecutionException) {
            logger.error("error trying to update instrument price in cache with isin: $isin")
            throw e
        }
    }

    fun deleteInstrument(isin: String) {
        try {
            cache.invalidate(isin)
        } catch (e: ExecutionException) {
            logger.error("error trying to invalidate cache for isin: $isin")
            throw e
        }
    }

    fun getAllInstruments(): ConcurrentMap<String, Double> {
        try {
            return cache.asMap()
        } catch (e: ExecutionException) {
            logger.error("Error trying to get all values")
            throw e
        }
    }
}
