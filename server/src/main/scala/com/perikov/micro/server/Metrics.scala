package com.perikov.micro.server

import io.prometheus.{client => prom}
import io.prometheus.client.CollectorRegistry

trait Metrics[F[_]]:
    val incRequests: F[Unit]
    val scrape: F[String]

object Metrics: 
    import cats.effect.{Sync,Resource}
    def apply[F[_]: Metrics]: Metrics[F]= summon
    private val counter = 
                prom
                    .Counter
                    .build()
                    .name("some_counter_total")
                    .help("Just a sample counter")
                    .register()
    prom.hotspot.DefaultExports.initialize()


    given onSync[F[_]](using sync: Sync[F]): Metrics[F] with
        val incRequests = sync.delay(counter.inc())
        val scrape = sync.delay {
            val writer = java.io.StringWriter(1024)
            prom.exporter.common.TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples())
            writer.toString
        }