
data <- read.table("/Users/john/xper-src/xper/unit_test/org/xper/acq/counter/data_segment.txt");

marker1 <- subset(data, V1==1);
marker2 <- subset(data, V1==2);

plot(marker1$V2, marker1$V3, col="red");
lines(marker2$V2, marker2$V3, col="blue");
