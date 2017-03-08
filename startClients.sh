#!/bin/bash
for i in `seq 1 100`;
do
   java cs455.scaling.client.Client localhost 8000 4 &
done

