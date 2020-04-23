package util.parameter_configuration.data.representation;

import util.parameter_configuration.data.handler.DataHandler;

import java.util.List;

public abstract class DataRepresentation<D extends DataHandler> {

    List<D> dataHandlerList;

    public DataRepresentation(List<D> dataHandlerList) {
        this.dataHandlerList = dataHandlerList;
    }

    public List<D> getDataHandlerList() {
        return dataHandlerList;
    }

    public void setDataHandlerList(List<D> dataHandlerList) {
        this.dataHandlerList = dataHandlerList;
    }
}
