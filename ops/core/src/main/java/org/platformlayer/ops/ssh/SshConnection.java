package org.platformlayer.ops.ssh;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyPair;

import org.platformlayer.TimeSpan;
import org.platformlayer.ops.process.ProcessExecution;

/**
 * Implements an SSH connection. Abstract class that is either implemented by a MINA SSH library binding
 * 
 * @author justinsb
 * 
 */
public abstract class SshConnection implements Closeable {
    private InetAddress host;
    private int port = 22;
    private String user = "root";
    private KeyPair keyPair;
    private IServerKeyVerifier serverKeyVerifier;

    public static final TimeSpan DEFAULT_SSH_EXECUTE_TIMEOUT = new TimeSpan("15s");

    public static final TimeSpan DEFAULT_SSH_CONNECT_TIMEOUT = new TimeSpan("15s");
    public static final TimeSpan DEFAULT_SSH_KEY_EXECUTE_TIMEOUT = new TimeSpan("15s");

    @Override
    public abstract void close();

    public interface ProcessStartListener {
        void startedProcess(OutputStream processStdIn) throws IOException;
    }

    public ProcessExecution sshExecute(String command, TimeSpan timeout) throws SshException, IOException, InterruptedException {
        ProcessExecution execution = sshExecute0(command, timeout);

        return execution;
    }

    protected abstract ProcessExecution sshExecute0(String command, TimeSpan timeout) throws SshException, IOException, InterruptedException;

    public ProcessExecution sshExecute(String command) throws SshException, IOException, InterruptedException {
        return sshExecute(command, DEFAULT_SSH_EXECUTE_TIMEOUT);
    }

    public void sshCopyData(InputStream srcData, long dataLength, String remoteFile, String mode) throws IOException, InterruptedException, SshException {
        if (mode == null) {
            throw new IllegalArgumentException("Mode must be specified");
        }

        sshCopyData0(srcData, dataLength, remoteFile, mode);
    }

    protected abstract void sshCopyData0(InputStream fileData, long dataLength, String remoteFile, String mode) throws IOException, InterruptedException, SshException;

    public byte[] sshReadFile(String remoteFilePath) throws IOException, InterruptedException, SshException {
        byte[] retval = sshReadFile0(remoteFilePath);
        return retval;
    }

    protected abstract byte[] sshReadFile0(String remoteFilePath) throws IOException, InterruptedException, SshException;

    public static void safeClose(SshConnection sshConnection) {
        if (sshConnection != null) {
            sshConnection.close();
        }
    }

    public SocketAddress getRemoteAddress() {
        return new InetSocketAddress(getHost(), getPort());
    }

    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair privateKey) {
        this.keyPair = privateKey;
    }

    public IServerKeyVerifier getServerKeyVerifier() {
        return serverKeyVerifier;
    }

    public void setServerKeyVerifier(IServerKeyVerifier serverKeyVerifier) {
        this.serverKeyVerifier = serverKeyVerifier;
    }

}
