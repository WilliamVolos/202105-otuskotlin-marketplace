package ru.otus.otuskotlin.markeplace.springapp.service

import marketplace.stubs.Bolt
import org.springframework.stereotype.Service
import ru.otus.otuskotlin.marketplace.backend.common.context.MpContext
import ru.otus.otuskotlin.marketplace.backend.common.models.AdIdModel

@Service
class OfferService {

    fun readOffers(context: MpContext): MpContext {
        val lastAdModelIdOnPage = context.requestPage.lastId
        val shouldReturnSingle = lastAdModelIdOnPage != AdIdModel.NONE

        return if (shouldReturnSingle) {
            context.apply {
                responseAds.add(Bolt.getModel())
            }
        } else {
            context.apply {
                responseAds.addAll(Bolt.getModels())
                responsePage = requestPage
            }
        }
    }
}