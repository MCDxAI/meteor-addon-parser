package com.cope.addonparser.model;

import java.util.ArrayList;
import java.util.List;

public class ScanSummary {
    public int jarCount;
    public int successCount;
    public int failureCount;
    public List<String> failedJars = new ArrayList<>();
    public List<String> outputFiles = new ArrayList<>();
}
