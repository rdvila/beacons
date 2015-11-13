#!/usr/bin/Rscript

library(ggplot2)
library(grid)
library(gridExtra)
library(Cairo)

argv <- commandArgs(TRUE)
model <- argv[1]

CairoPNG(filename=paste("min_max_media_",gsub(" ", "_", tolower(model)),".png",sep=""), width=1500, height=500)
a <- read.csv(file="beacon_A.log", head=FALSE, sep=",")
fa <- ggplot(data=a, aes(x=V6, y=V3)) + stat_summary(fun.y=mean, fun.ymin=min, fun.ymax=max, colour="red") + xlab("Distance (cm)") + ylab("RSSI (dB)") + coord_cartesian(ylim=c(-45, -100), xlim=c(-5, 355)) + scale_y_continuous(breaks=seq(-100, -45, 5)) + scale_x_continuous(breaks=seq(0, 350, 25)) + ggtitle(paste("RSSI Min/Max and Mean Beacon A (", model ,")", sep=""))


b <- read.csv(file="beacon_B.log", head=FALSE, sep=",")
fb <- ggplot(data=b, aes(x=V6, y=V3)) + stat_summary(fun.y=mean, fun.ymin=min, fun.ymax=max, colour="dark green") + xlab("Distance (cm)") + ylab("RSSI (dB)") + coord_cartesian(ylim=c(-45, -100), xlim=c(-5, 355)) + scale_y_continuous(breaks=seq(-100, -45, 5)) + scale_x_continuous(breaks=seq(0, 350, 25)) + ggtitle(paste("RSSI Min/Max and Mean Beacon B (", model, ")", sep=""))

c <- read.csv(file="beacon_C.log", head=FALSE, sep=",")
fc <- ggplot(data=c, aes(x=V6, y=V3)) + stat_summary(fun.y=mean, fun.ymin=min, fun.ymax=max, colour="blue") + xlab("Distance (cm)") + ylab("RSSI (dB)") + coord_cartesian(ylim=c(-45, -100), xlim=c(-5, 355)) + scale_y_continuous(breaks=seq(-100, -45, 5)) + scale_x_continuous(breaks=seq(0, 350, 25)) + ggtitle(paste("RSSI Min/Max and Mean Beacon C (", model, ")", sep=""))

grid.arrange(fa,fb,fc, ncol=3)
dev.off()

CairoPNG(filename=paste("media_sinal_dist_",gsub(" ", "_", tolower(model)),".png",sep=""), width=500, height=500)
all <- read.csv(file="beacon_all_mean.txt", head=FALSE, sep=",")
ggplot(data=all, aes(x=V3, y=V2, group=V1, colour=V1)) + geom_line() + geom_point() + xlab("Distance (cm)") + ylab("RSSI (dB)") + coord_cartesian(ylim=c(-45, -100), xlim=c(-5, 355)) + scale_y_continuous(breaks=seq(-100, -45, 5)) + scale_x_continuous(breaks=seq(0, 350, 25)) + scale_colour_discrete(name="Beacons") + ggtitle(paste("Distance Signal Mean (", model, ")", sep=""))
dev.off()

CairoPNG(filename=paste("media_sinal_dist_0_1500_",gsub(" ", "_", tolower(model)),".png",sep=""), width=500, height=500)
all500 <- read.csv(file="beacon_all-0-1500_mean.txt", head=FALSE, sep=",")
ggplot(data=all500, aes(x=V3, y=V2, group=V1, colour=V1)) + geom_line() + geom_point() + xlab("Distance (cm)") + ylab("RSSI (dB)") + coord_cartesian(ylim=c(-45, -100), xlim=c(-25, 1525)) + scale_y_continuous(breaks=seq(-100, -45, 5)) + scale_x_continuous(breaks=seq(0, 1500, 500)) + scale_colour_discrete(name="Beacons") + ggtitle(paste("Distance Signal Mean from 0 to 15 meters (", model, ")", sep=""))
dev.off()

