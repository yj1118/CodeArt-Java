package apros.codeart.gate.service;

import apros.codeart.App;
import apros.codeart.PreApplicationStart;
import apros.codeart.RunPriority;

@PreApplicationStart(method = "initialize", priority = RunPriority.Framework_Low)
public class PreStart {
    public static void initialize() {
        
        App.setup("service");
    }
}