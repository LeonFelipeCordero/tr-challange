package com.tr.api.config

import com.tr.api.model.Instrument
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import javax.annotation.PostConstruct

//@Configuration
//@DependsOn("reactiveMongoTemplate")
//class MongoConfig(private val reactiveMongoTemplate: ReactiveMongoTemplate) {

//    @PostConstruct
//    fun init() {
//        reactiveMongoTemplate
//                .createCollection(Instrument::class.java)
//                .subscribe()
//    }
//}
