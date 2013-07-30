import os

def loadCsv(filename, offset=0):
	openFile = open(filename, 'r')
	someList = list(openFile)
	openFile.close()
	theData = []

	for i in range(offset, len(someList)):

		lineParts = someList[i].split(',')
		theData.append(lineParts)

	return theData


def removeMetaFile(folder):
	fileList = getFiles(folder)
	if len(fileList) < 2:
		return
	for i in range(0, len(fileList)):
		if(fileList[i][fileList[i].rfind('/')+1:] == 'meta.csv'):
			os.remove(fileList[i])



def getMetaData(folder):
	
	data = None
	
	fileList = getFiles(folder)
	appendToFile(folder+'meta.csv', 'result, steps')

	for i in range ( 0, len(fileList) ):
		data = loadCsv(fileList[i])
		for j in range (0, len(data)):
			if len(data[j]) >=2  and (data[j][1] == 'FOUND' or data[j][1] == 'RUNAWAY'):
				appendToFile(folder+'meta.csv', str(data[j][1]) + ',' + str(j/5.0) )
					
		
def appendToFile(filePath, line):
	fileHandle = open(filePath, 'a')
	fileHandle.write(line+'\n');
	fileHandle.close()		
	

def getFiles(folder):
	children = os.listdir(folder)
	filesOnly = []
	for c in children:
		if os.path.isfile(folder + '/'+ c):
			filesOnly.append(folder + '/'+ c )
	return filesOnly

def makeMetaFiles(outputRoot, spot):
	rootPath = outputRoot + spot+ '/'
	folderList = os.listdir(rootPath)
	for child in folderList:
		if(os.path.isdir(rootPath + child )):
			getMetaData(rootPath + child)

def summarizeSearchResults(outputroot):
	outs = getFiles(outputroot)
	uavs = {}
	agents = {}
	meta = {}
	# count, steps
	uavs['mower'] = [0,0,0]
	uavs['wander'] = [0,0,0]
	uavs['team'] = [0,0,0]
	meta['meta'] = [0,0]
	meta['nc'] = [0,0]
	meta['lost'] = [0,0]


	for i in range ( 0, len(outs) ):
		key = 'nc'
		if(outs[i].rfind('MOWER') != -1):
			key = 'mower'
		
		elif( outs[i].rfind('TEAM') != -1):
			key = 'team'

		elif( outs[i].rfind('WANDER') != -1):
			key = 'wander'

		print 'loading ' + outs[i]
		startLostAgentName = outs[i].rfind('/')+1
		lostAgentName = outs[i][startLostAgentName : outs[i].find('1', startLostAgentName)-1]
		if lostAgentName not in agents:
			agents[lostAgentName] = [0,0]

		if lostAgentName + '_' + key not in agents:
			agents[lostAgentName + '_' + key] = [0,0]

		data = loadCsv(outs[i], 1)
		
		for j in range(0,len(data)):

			if(data[j][0] == 'FOUND'):
				uavs[key][0] += 1
				uavs[key][1] += float(data[j][1])
				agents[lostAgentName][1] +=1
				agents[lostAgentName + '_' + key][1] +=1
			elif(data[j][0] == 'RUNAWAY'):
				meta['lost'][0] += 1
				meta['lost'][1] += float(data[j][1])
				uavs[key][2] += 1
				agents[lostAgentName][0] +=1
				agents[lostAgentName + '_' + key][0] +=1
			if(data[j][0] != 'result'):
				meta['meta'][0] += 1


				

	return [uavs, agents, meta]

def writeDict(outfile, dictthing):
	appendToFile(outfile,"key,value")
	for key in dictthing:
		line=[]
		line.append(key)
		for j in range (0 , len(dictthing[key])):	
			line.append(dictthing[key][j])	

		line = [str(x) for x in line]
		csvline = ",".join(line)
		appendToFile(outfile,csvline)

spot = 'SPOT_2'
makeMetaFiles('C:/agentout/searchresults/',spot)
data = summarizeSearchResults('C:/agentout/searchresults/'+spot)

writeDict('C:/agentout/searchresults/' + spot + '/uavs.csv', 	data[0] )
writeDict('C:/agentout/searchresults/' + spot + '/agents.csv', 	data[1] )
writeDict('C:/agentout/searchresults/' + spot + '/meta.csv', 	data[2] )

