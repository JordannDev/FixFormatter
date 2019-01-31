# FAA Fix Formatter
##### For VATSIM use only.

### How to use it

Simply get the FIX.txt file from the FAA then run the application like this: 

```
java -jar FixFormat.jar (ARTCC CODE) (FIX FILE)
java -jar FixFormat.jar ZME FIX.txt
```

You can get the FAA FIX.txt file from this URL: https://www.faa.gov/air_traffic/flight_info/aeronav/aero_data/NASR_Subscription/
It will automatically format it into a format usable in .sct2 you may need to convert it using other applications for it to be compatible with other clients.
Also keep in mind; I made this for my specific ARTCC, so double check that the format is correct before assuming it works.

Here an example of the output:

Input Data
```
FIX1AARTA                         ALABAMA                       K734-36-21.290N 087-16-24.750WFIX                                                                                                                   YWAYPOINT       AARTAZME ZME                               NNN                                                                                                                                                                                                
```

Output
```
AARTA N034.36.21.290 W087.16.24.750
```

Feel free to modify, its all commandline right now, but maybe one day if im bored ill put a GUI in front of it.

