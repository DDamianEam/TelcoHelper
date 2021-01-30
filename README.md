# TelcoHelper

Signalling Point Code (SPC) in SS7 network is an equivalent of IP address in Internet network.
Contrary to IP address, SPC can have at least two formats - 14-bit and 24-bit length.
The 14-bit SPC are known as International or ITU-T format. The 24-bit format in turn is common in USA.

To get thing more complicated, the SPC can have also different visuual representation. The most common is a single decimal or hexadecimal value.
In international routing the "3-8-3" format is used often.
Different countries have their own regulations to specify the SPC representation - we can see 7-7, 8-8-8, 5-2-5-2 and so on formats.

The TelcoHelper software is a tool to provide translation between different formats as well representations of SPC.

This implementation uses Java Swing framework - this is very simple use case with static classes only.

## INSTRUCTIONS

You need Maven to compile & build this project.

Compile:

mvn compile


Run:

cd target/classes

java info.danos.Main


DISCLAIMER

This source code is the proof-of-concept implementation of SPC conversion algorithms. 
The purpose of the software is purely experimental.
