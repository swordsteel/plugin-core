package ltd.lulz.plugin.extension

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GitExtensionTest {

    companion object {
        const val GIT_HASH_SHA_1 = "0a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t"
        const val SHORT_GIT_HASH = "0a1b2c3d"
        const val SNAPSHOT_HASH_VERSION = "0.0.0.0a1b2c3d-SNAPSHOT"
        const val SNAPSHOT_VERSION = "0.0.0-SNAPSHOT"
        const val VERSION = "0.0.0"

        const val BRANCH_DEVELOP = "develop"
        const val BRANCH_FEATURE = "feature/ABC-123"
        const val BRANCH_MASTER = "master"
        const val EXTENSION = "git"
        const val PLUGIN_ID = "ltd.lulz.plugin.core"
        const val REF_HEAD = "HEAD"
        const val UNAVAILABLE = "n/a"
    }

    private val gitMock: Git = mockk()
    private val refMock: Ref = mockk()
    private val repositoryMock: Repository = mockk()
    private val objectIdMock: ObjectId = mockk()

    lateinit var name: String
    lateinit var project: Project

    @BeforeEach
    fun buildUp() {
        project = ProjectBuilder.builder().build()
        project.version = SNAPSHOT_VERSION
        project.pluginManager.apply(PLUGIN_ID)
        mockkStatic(Git::class)

        every { Git.open(any()) } returns gitMock
        every { gitMock.repository } returns repositoryMock
        every { repositoryMock.branch } returns null
        every { repositoryMock.exactRef(any()) } returns refMock
        every { refMock.target } returns refMock
        every { refMock.name } returns null
        every { refMock.objectId } returns objectIdMock
        every { objectIdMock.name } returns null

        justRun { gitMock.close() }
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Git::class)
    }

    @ParameterizedTest
    @CsvSource(
        "$REF_HEAD, true",
        "$BRANCH_FEATURE, false",
    )
    fun `current ref is head`(ref: String, expected: Boolean) {
        // given
        every { refMock.name } returns ref

        // when
        val extension = project.extensions.getByName(EXTENSION) as GitExtension

        // then
        assertEquals(expected, extension.isHead())
    }

    @ParameterizedTest
    @CsvSource(
        ", $UNAVAILABLE",
        "$BRANCH_FEATURE, $BRANCH_FEATURE",
    )
    fun `get current branch`(branch: String?, expected: String) {
        // given
        every { repositoryMock.branch } returns branch

        // when
        val extension = project.extensions.getByName(EXTENSION) as GitExtension

        // then
        assertEquals(expected, extension.currentBranch())
    }

    @ParameterizedTest
    @CsvSource(
        ", $UNAVAILABLE",
        "$GIT_HASH_SHA_1, $SHORT_GIT_HASH",
    )
    fun `get current short hash`(hash: String?, expected: String) {
        // given
        every { objectIdMock.name } returns hash

        // when
        val extension = project.extensions.getByName(EXTENSION) as GitExtension

        // then
        assertEquals(expected, extension.currentShortHash())
    }

    @Test
    fun `get version - version without snapshot`() {
        // given
        project.version = VERSION
        every { objectIdMock.name } returns GIT_HASH_SHA_1

        // when
        val extension = project.extensions.getByName(EXTENSION) as GitExtension

        // then
        assertEquals(VERSION, extension.version())
    }

    @Test
    fun `get version - short hash is blank`() {
        // when
        val extension = project.extensions.getByName(EXTENSION) as GitExtension

        // then
        assertEquals(SNAPSHOT_VERSION, extension.version())
    }

    @ParameterizedTest
    @CsvSource(
        ", , $SNAPSHOT_HASH_VERSION",
        ", $REF_HEAD, $SNAPSHOT_VERSION",
        "$BRANCH_MASTER, , $SNAPSHOT_VERSION",
        "$BRANCH_MASTER, , $SNAPSHOT_VERSION",
        "$BRANCH_DEVELOP, , $SNAPSHOT_VERSION",
        "$BRANCH_FEATURE, , $SNAPSHOT_HASH_VERSION",
        "$BRANCH_MASTER, $REF_HEAD, $SNAPSHOT_VERSION",
        "$BRANCH_MASTER, $REF_HEAD, $SNAPSHOT_VERSION",
        "$BRANCH_DEVELOP, $REF_HEAD, $SNAPSHOT_VERSION",
        "$BRANCH_FEATURE, $REF_HEAD, $SNAPSHOT_VERSION",
    )
    fun `get version - different branches`(branch: String?, ref: String?, expected: String) {
        // given
        every { repositoryMock.branch } returns branch
        every { objectIdMock.name } returns GIT_HASH_SHA_1
        every { refMock.name } returns ref

        // when
        val extension = project.extensions.getByName(EXTENSION) as GitExtension

        // then
        assertEquals(expected, extension.version())
    }
}
