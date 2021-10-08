package ru.otus.otuskotlin.marketplace.backend.repo.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder
import com.datastax.oss.driver.internal.core.type.codec.extras.enums.EnumNameCodec
import com.datastax.oss.driver.internal.core.type.codec.registry.DefaultCodecRegistry
import com.datastax.oss.driver.internal.core.util.concurrent.CompletableFutures
import org.testcontainers.containers.CassandraContainer
import ru.otus.otuskotlin.marketplace.backend.common.models.AdModel
import ru.otus.otuskotlin.marketplace.backend.common.models.AdVisibilityModel
import ru.otus.otuskotlin.marketplace.backend.common.models.DealSideModel
import ru.otus.otuskotlin.marketplace.backend.common.models.PermissionModel
import ru.otus.otuskotlin.marketplace.backend.repo.common.IRepoAd
import ru.otus.otuskotlin.marketplace.backend.repo.test.RepoAdCreateTest
import ru.otus.otuskotlin.marketplace.backend.repo.test.RepoAdDeleteTest
import ru.otus.otuskotlin.marketplace.backend.repo.test.RepoAdReadTest
import ru.otus.otuskotlin.marketplace.backend.repo.test.RepoAdSearchTest
import ru.otus.otuskotlin.marketplace.backend.repo.test.RepoAdUpdateTest
import java.net.InetSocketAddress

class RepoAdCassandraCreateTest : RepoAdCreateTest() {
    override val repo: IRepoAd = TestCompanion.repository(initObjects)
}

class RepoAdCassandraDeleteTest : RepoAdDeleteTest() {
    override val repo: IRepoAd = TestCompanion.repository(initObjects)
}

class RepoAdCassandraReadTest : RepoAdReadTest() {
    override val repo: IRepoAd = TestCompanion.repository(initObjects)
}

class RepoAdCassandraSearchTest : RepoAdSearchTest() {
    override val repo: IRepoAd = TestCompanion.repository(initObjects)
}

class RepoAdCassandraUpdateTest : RepoAdUpdateTest() {
    override val repo: IRepoAd = TestCompanion.repository(initObjects)
}

class TestCasandraContainer : CassandraContainer<TestCasandraContainer>()

object TestCompanion {
    val container by lazy { TestCasandraContainer().apply { start() } }

    val codecRegistry by lazy {
        DefaultCodecRegistry("default").apply {
            register(EnumNameCodec(AdVisibilityModel::class.java))
            register(EnumNameCodec(DealSideModel::class.java))
            register(EnumNameCodec(PermissionModel::class.java))
        }
    }

    val session by lazy {
        CqlSession.builder()
            .addContactPoint(InetSocketAddress(container.host, container.getMappedPort(CassandraContainer.CQL_PORT)))
            .withLocalDatacenter("datacenter1")
            .withAuthCredentials(container.username, container.password)
            .withCodecRegistry(codecRegistry)
            .build()
    }

    val mapper by lazy { CassandraMapper.builder(session).build() }

    fun repository(initObjects: List<AdModel>): RepoAdCassandra {
        val keyspace = "data"

        session.execute(
            SchemaBuilder
                .createKeyspace(keyspace)
                .ifNotExists()
                .withSimpleStrategy(1)
                .build()
        )
        session.execute(AdCassandraDTO.table(keyspace, AdCassandraDTO.TABLE_NAME))
        session.execute(AdCassandraDTO.titleIndex(keyspace, AdCassandraDTO.TABLE_NAME))

        val dao = mapper.adDao(keyspace, AdCassandraDTO.TABLE_NAME)
        CompletableFutures
            .allDone(initObjects.map { dao.create(AdCassandraDTO(it)) })
            .toCompletableFuture()
            .get()
        return RepoAdCassandra(dao)
    }
}