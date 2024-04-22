package apros.codeart.mq.rpc.server;

import apros.codeart.dto.DTObject;
import apros.codeart.mq.TransferData;

public interface IRPCHandler {
	TransferData Process(String method, DTObject args);
}
