|sFinder|sFinder := StructureFinder new.sFinder path: 'C:\your\computer\path\testFiles'."path to project root folder"sFinder inputFolder: 'java'. "folder of files to be loaded"sFinder k: 15. "number of max centroids"sFinder kmin: 5. "number of min centroids, only used for mode = 2, interval"sFinder mode: 2. "1= single k, 2 = intevall"sFinder alpha: -3. "Threshold for elobow method"sFinder iterations: 25. "Number of k-mean iterations""Add which Representations to add via instantiation of Representor Object"sFinder typeRep: TypeStructureRepresentor new.sFinder distRep: DistanceAllRepresentor new.sFinder weigthRep: WeightSummaryRepresentor new.sFinder multiline: 1. "Additional option to use more than one line in NewLineStamentmaker"sFinder run.