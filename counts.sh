
for file in paper-experiments-out/*/*.csv
do
    echo $file
    tail --lines=1 $file
done