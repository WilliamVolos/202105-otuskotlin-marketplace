package ru.otus.otuskotlin.marketplace.logics.chains.stubs

import ru.otus.otuskotlin.marketplace.backend.common.context.CorStatus
import ru.otus.otuskotlin.marketplace.backend.common.context.MpContext
import ru.otus.otuskotlin.marketplace.backend.common.exceptions.MpStubCaseNotFound
import ru.otus.otuskotlin.marketplace.backend.common.models.MpStubCase
import ru.otus.otuskotlin.marketplace.common.cor.ICorChainDsl
import ru.otus.otuskotlin.marketplace.common.cor.handlers.chain
import ru.otus.otuskotlin.marketplace.common.cor.handlers.worker
import ru.otus.otuskotlin.marketplace.stubs.Bolt

internal fun ICorChainDsl<MpContext>.adUpdateStub(title: String) = chain {
    this.title = title
    on { status == CorStatus.RUNNING && stubCase != MpStubCase.NONE }

    worker {
        this.title = "Успешный стабкейс для UPDATE"
        on { stubCase == MpStubCase.SUCCESS }
        handle {
            responseAd = requestAd.copy(permissions = Bolt.getModel().permissions)
            status = CorStatus.FINISHING
        }
    }

    worker {
        this.title = "Обработка отсутствия подходящего стабкейса"
        on { status == CorStatus.RUNNING }
        handle {
            status = CorStatus.FAILING
            addError(
                e = MpStubCaseNotFound(stubCase.name),
            )
        }
    }

}
