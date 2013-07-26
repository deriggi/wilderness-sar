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

def runner():
	children = []
	children.append('C:/agentout/SPOT_1.csv')
	children.append('C:/agentout/SPOT_2.csv')
	avgDict = averageColumns(3, children)
	stdvDict = averageColumns(4, children)
	
	dicts = []
	dicts.append(stdvDict)
	dicts.append(avgDict)
	print( mergeDictionaries(dicts) )

def appendToFile(filePath, line):
	fileHandle = open(filePath, 'a')
	fileHandle.write(line+'\n');
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
outfile = 'C:/agentout/rank/allspotsummary.csv'
# if os.path.isfile(outfile):
# 	os.remove(outfile)
# ranks = runner('C:/agentout/rank/')

runner()

