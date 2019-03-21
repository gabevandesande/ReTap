package edu.ucsc.retap.retap.common.di

import javax.inject.Scope

/**
 * Scope for dependencies that follow activity lifecycle.
 */
@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope
