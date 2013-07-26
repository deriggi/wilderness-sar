import os
import math

def loadCsv(filename):
	openFile = open(filename, 'r')
	someList = list(openFile)
	openFile.close()

	theData = []

	for i in range(1, len(someList)):

		lineParts = someList[i].split(',')
		theData.append(lineParts)

	return theData

def getFiles(folder):
	children = os.listdir(folder)
	filesOnly = []
	for c in children:
		filesOnly.append(folder + '/'+ c )
	return filesOnly

def getHeaderFromFile(somefile):
	openfile = open(somefile, 'r')
	firstline = list(openfile)[0];
	openfile.close()

	return firstline

def runner():
	outfile = 'C:/agentout/rank/allspotsummary.csv'
	children = []
	children.append('C:/agentout/SPOT_1.csv')
	children.append('C:/agentout/SPOT_2.csv')
	
	headerLine =  getHeaderFromFile('C:/agentout/SPOT_1.csv')
	linelength = len(headerLine.split(','))

	dicts = []

	for i in range (1, linelength):
		dicts.append(averageColumns(i,children))


	#todo ,write these to a summary file!
	if os.path.isfile(outfile):
		os.remove(outfile)

	appendToFileRaw(outfile,headerLine)
	writeDictList(mergeDictionaries(dicts) , outfile)

# def fromDictEntryToLine()
def writeDictList(dictlist, outfile):
	for key in dictlist:
		line=[]
		line.append(key)
		line.extend(dictlist[key])
		line = [str(x) for x in line]
		csvline = ",".join(line)
		appendToFile(outfile,csvline)



def appendToFile(filePath, line):
	fileHandle = open(filePath, 'a')
	fileHandle.write(line+'\n');
	fileHandle.close()

def appendToFileRaw(filePath, line):
	fileHandle = open(filePath, 'a')
	fileHandle.write(line);
	fileHandle.close()

# average the column in the file set
def averageColumns(index, files):
	behaves = {}
	for i in range(0, len(files)):
		data = loadCsv(files[i])
		for j in range (0, len(data)):
			if data[j][0] not in behaves:
				behaves[ data[j][0] ] = 0
			behaves[ data[j][0] ] += float(data[j][index])

	for key in behaves:
		behaves[key] = behaves[key]/len(files)

	return behaves

# given a list of dictionaries merge to key, n1, n2
def mergeDictionaries(manyDicts):
	masterDict = {}

	for i in range(0, len(manyDicts)):
		for j in manyDicts[i]:
			if j not in masterDict:
				masterDict[j] = []
			
			masterDict[j].append( manyDicts[i][j] )
	return masterDict

# def collectRanks(metaFiles, column, outputfolder):
	# get a meta csv, sort it by column, write it to rank folder by columnname file
	# loadCsv()

# give this a folder to a list of csvs with ranks
# if os.path.isfile(outfile):
# 	os.remove(outfile)
# ranks = runner('C:/agentout/rank/')

runner()

