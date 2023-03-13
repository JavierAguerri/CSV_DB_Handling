#!/bin/bash

# Set environment variables
export DB_URL="jdbc:mysql://localhost:3306/"
export DB_NAME="ordersDB"
export DB_USER="javier"
export DB_PASS="kl3y-i_WMnD3CtpN@B2r-"

# Set working directory to the root of your project
cd csvdbhandling

long=false

while getopts ":l" opt; do
  case $opt in
    l)
      long=true
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

if $long; then
  export ORDERS_LINK="https://drive.google.com/u/0/uc?id=1lLMqoS4dxaRM3NPFUsacq0Ca8_6RrygA&export=download&confirm=t&uuid=ecd60b84-727b-4d85-bc9b-a95d69fc7b59&at=ALgDtsxD9bPy7fss9vZTm2ocXLJ-:1678668455845"
else
  export ORDERS_LINK="https://drive.google.com/u/0/uc?id=1pkmcx7M1KzVRwQxRBkWBkg6GANFN2RRw&export=download"
fi

# Run Java program
time java -jar target/csvdbhandling-1.0-jar-with-dependencies.jar

