package com.fsck.k9.backend.imap

import com.fsck.k9.mail.store.imap.ImapFolder
import com.fsck.k9.mail.store.imap.ImapStore
import com.fsck.k9.mail.store.imap.OpenMode
import net.thunderbird.core.common.mail.Flag
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CommandMarkAllAsReadTest {

    @Test
    fun `markAllAsRead should open folder in READ_WRITE mode, set SEEN flag and close folder`() {
        val folderServerId = "folder1"
        val remoteFolder = mock<ImapFolder>()
        val imapStore = mock<ImapStore> {
            on { getFolder(folderServerId) } doReturn remoteFolder
        }

        val command = CommandMarkAllAsRead(imapStore)

        command.markAllAsRead(folderServerId)

        inOrder(remoteFolder) {
            verify(remoteFolder).open(OpenMode.READ_WRITE)
            verify(remoteFolder).setFlagsForAllMessages(setOf(Flag.SEEN), true)
            verify(remoteFolder).close()
        }
    }

    @Test(expected = RuntimeException::class)
    fun `markAllAsRead should close folder even if setting flags throws exception`() {
        val folderServerId = "folder1"
        val remoteFolder = mock<ImapFolder> {
            on { setFlagsForAllMessages(setOf(Flag.SEEN), true) } doThrow RuntimeException("Test Exception")
        }
        val imapStore = mock<ImapStore> {
            on { getFolder(folderServerId) } doReturn remoteFolder
        }

        val command = CommandMarkAllAsRead(imapStore)

        try {
            command.markAllAsRead(folderServerId)
        } finally {
            verify(remoteFolder).close()
        }
    }
}
