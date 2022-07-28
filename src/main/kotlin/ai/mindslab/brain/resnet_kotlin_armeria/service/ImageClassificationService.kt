package ai.mindslab.brain.resnet_kotlin_armeria.service

import org.slf4j.LoggerFactory

class ImageClassificationService {
    private val logger = LoggerFactory.getLogger(ImageClassificationService::class.qualifiedName)

    fun classify(imageTensor: FloatArray, shape: LongArray): FloatArray {
        return FloatArray(20)
    }
}