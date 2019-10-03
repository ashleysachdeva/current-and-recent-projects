import nltk
from nltk import FreqDist
import glob
import math
import re
import textblob
from textblob import TextBlob

########################
### GLOBAL VARIABLES ###
########################

## hand-crafted list of stop words
stops = {"'d", "'ll", "'m", "'ve", "'t", "'s", "'re", "a", "about", "above", "after", "again", "against", "ain", "all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "just", "let's", "me", "mightn't", "more", "most", "mustn't", "my", "myself", "needn't", "no", "nor", "not", "now", "o", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "should've", "shouldn't", "so", "some", "such", "t", "than", "that", "that'll", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "will", "with", "won't", "would", "wouldn", "wouldn't", "y'all", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", ",", ".", "!", "?", "'", '"', "I", "i"}

# these store the words used in positive and negative reviews
# these will be populated in read_in_training_data()
poswords = []
negwords = []

# these store the NB probability for each positive and negative word
# these will be populated in calculate_nb_probabilities()
poswordprobs = {}
negwordprobs = {}

# these store the smoothed NB probability for each positive and negative word
# these will be populated in calculate_smooth_nb_probabilities()
smooth_poswordprobs = {}
smooth_negwordprobs = {}


#################################
### FUNCTION TO READ IN DATA ###
#################################
# You don't need to modify this.
# This returns the lists of positive words 
# and negative words from the training data.

def read_in_training_data():


    ## Read in all positive reviews
    ## We create a set of unique words for each review. 
    ## We then add that set of words as a list to the master list of positive words.
    positivewords = []
    allpos = glob.glob("pos/*")
    for filename in allpos:
        f = open(filename)
        thesewords = set()
        for line in f:
            words = line.rstrip().split()
            for w in words:
                if w not in stops:
                    thesewords.add(w)
        f.close()
        positivewords.extend(list(thesewords))
    
    print(len(positivewords), "positive tokens found!")
    print(len(set(positivewords)), "positive types found!")
    
    
    ## Read in all negative reviews
    ## We create a set of unique words for each review.
    ## We then add that set of words as a list to the master list of negative words.
    negativewords = []
    allneg = glob.glob("neg/*")
    for filename in allneg:
        f = open(filename)
        thesewords = set()
        for line in f:
            words = line.rstrip().split()
            for w in words:
                if w not in stops:
                    thesewords.add(w)
        f.close()
        negativewords.extend(list(thesewords))
    
    print(len(negativewords), "negative tokens found!")
    print(len(set(negativewords)), "negative types found!")
    return(positivewords, negativewords)



######################################
### FUNCTIONS TO PREDICT SENTIMENT ###
######################################

## FUNCTION USING USER-DEFINED WORDS TO PREDICT SENTIMENT
# You just need to fill in your own keywords below.

def user_defined_keywords(reviewwords):


    #########################################
    ##### YOUR PART A CODE STARTS HERE ######
    #########################################

    # enter your keywords in the lists below
    positive_keywords = ["strong", "big", "fun", "surprise", "fantastic", "success", "friends", "powerful",
"happiness", "outstanding"]
    negative_keywords = ["critique", "bad", "terribly", "kill", "never", "unacceptable", "lame",
"horrible", "incorrect", "wrong"]
    

    #########################################
    ##### YOUR PART A CODE ENDS HERE ########
    #########################################


    # If there are more positive than negative keywords,
    # return "pos". Otherwise, return "neg".

    sentiment = 0
    for w in reviewwords:
        if w in positive_keywords:
            sentiment += 1
        if w in negative_keywords:
            sentiment -=1

    if sentiment > 0:
        return "pos"

    return "neg"


## FUNCTION TO CALCULATE NAIVE BAYES PROBABILITIES 
# You will be writing most of this function.
def calculate_nb_probabilities():

    ## GOAL: Populate these two dicts, where each
    ##      key = word from poswords or negwords (created for you above)
    ##      value = NB probability for that word in that class (calculated by you here)

    poswordprobs = {}
    negwordprobs = {}

    #########################################
    ##### YOUR PART B CODE STARTS HERE ######
    #########################################

    ## Create a FreqDist for poswords below.
    freqDistPos = FreqDist(poswords)
    countpos = len(freqDistPos)
    ## Create a FreqDist for negwords below.
    freqDistNeg = FreqDist(negwords)
    countneg = len(freqDistNeg)
    ## Loop through your poswords FreqDist, and calculate the
    ## probability of each word in the positive class, like this:
    ## P(word|pos) = count(word) / total number of positive tokens
    ## where count(word) is what you get from the FreqDist for poswords.
    ## Store the results in poswordprobs.
    ## USE LOGS!!!
    
    for word in freqDistPos:
        value = freqDistPos.get(word)
        prob = math.log(value / countpos)
        poswordprobs[word] = prob


    ## Now, loop through your negwords FreqDist, and calculate the
    ## probability of each word in the negative class, like this:
    ## P(word|neg) = count(word) / total number of negative tokens
    ## where count(word) is what you get from the FreqDist for negwords.
    ## Store the results in negwordprobs.
    ## USE LOGS!!!
    
    for word in freqDistNeg:
        value = freqDistNeg.get(word)
        prob = math.log(value / countneg)
        negwordprobs[word] = prob



    #########################################
    ##### YOUR PART B CODE ENDS HERE ########
    #########################################

    return (poswordprobs, negwordprobs)


## FUNCTION USING NAIVE BAYES PROBS TO PREDICT SENTIMENT
# You don't need to modify this method, but it relies
# on the code you  wrote above.

def naive_bayes(reviewwords):

    # default probability for unseen words
    defaultprob = math.log(0.0000000000001)
    
    ### POSITIVE SCORE
    posscore = poswordprobs.get(reviewwords[0], defaultprob)
    for i in range(1, len(reviewwords)):
        posscore += poswordprobs.get(reviewwords[i], defaultprob)

    ### CALCULATE NEGATIVE SCORE
    negscore = negwordprobs.get(reviewwords[0], defaultprob)
    for i in range(1, len(reviewwords)):
        negscore += negwordprobs.get(reviewwords[i], defaultprob)

    if (posscore - negscore) >  0:
        return "pos"

    return "neg"



## FUNCTION TO CALCULATE SMOOTHED NAIVE BAYES PROBABILITIES 
# You will write most of this function.
def calculate_smooth_nb_probabilities():

    smooth_poswordprobs = {}
    smooth_negwordprobs = {}

    #########################################
    ##### YOUR PART C CODE STARTS HERE ######
    #########################################

    # Populate the above dictionaries just as you did in the unsmoothed
    # version, but use +1 smoothing so that you can handle unseen words.
    freqDistPosSmooth = FreqDist(poswords)
    freqDistNegSmooth = FreqDist(negwords)
    # +1 smoothing: when calculating the probabilities,
    # add 1 to every count found in the FreqDist for each class.
    # Divide the count by the number of types...
    #     *plus* the number of tokens for that class...
    #     *plus* 1 (for the count of the unseen word)

    # Don't forget to use logs.
    typesP= len(set(poswords))
    tokensP = len(poswords)
    typesN= len(set(negwords))
    tokensN = len(negwords)
    for word in freqDistPosSmooth:
        valueSmooth = freqDistPosSmooth.get(word)
        countposSmooth = valueSmooth+1
        probSmooth = math.log(countposSmooth/(typesP+tokensP+1))
        smooth_poswordprobs[word] = probSmooth
    for word in freqDistNegSmooth:
        valueSmooth = freqDistNegSmooth.get(word)
        countnegSmooth = valueSmooth+1
        probSmooth = math.log(countnegSmooth/(typesN+tokensN+1))
        smooth_negwordprobs[word] = probSmooth

    return (smooth_poswordprobs, smooth_negwordprobs)


## FUNCTION USING SMOOTHED NAIVE BAYES PROBS TO PREDICT SENTIMENT
# You will write most of this function.
def smooth_naive_bayes(reviewwords):

    # These are placeholders that allow the code to run.
    # You will calculate posscore and negscore below.
    posscore = 0
    negscore = 0

    # Adapt the code from naive_bayes() above to work here.
    # Use the smoothed probabilities you created above.
    typesPo= len(set(poswords))
    tokensPo = len(poswords)
    typesNe= len(set(negwords))
    tokensNe = len(negwords)
    
    
    defaultprobP = math.log(1/(typesPo + tokensPo +1))
    defaultprobN = math.log(1/(typesNe + tokensNe +1))
    ### POSITIVE SCORE
    posscore = smooth_poswordprobs.get(reviewwords[0], defaultprobP)
    for i in range(1, len(reviewwords)):
        posscore += smooth_poswordprobs.get(reviewwords[i], defaultprobP)

    ### CALCULATE NEGATIVE SCORE
    negscore = smooth_negwordprobs.get(reviewwords[0], defaultprobN)
    for i in range(1, len(reviewwords)):
        negscore += smooth_negwordprobs.get(reviewwords[i], defaultprobN)
    # Do not forget to create a separate defaultprob for
    # unseen words for the two classes, as follows.

    # The defaultprob for each class should be
    # the log of:
    # 1 (the count of the unseen word) divided by...
    #    the number of types in that class...
    #    *plus* the number of tokens in that class...
    #    *plus* 1

    #########################################
    ##### YOUR PART C CODE ENDS HERE ########
    #########################################


    if (posscore - negscore) >  0:
        return "pos"

    return "neg"


## FUNCTION FOR APPLYING TEXTBLOB's SENTIMENT TOOL TO A REVIEW
def calculate_textblob(review):

    #########################################
    ##### YOUR PART D CODE STARTS HERE ######
    #########################################

    # First, tead the documentation for the textblob sentiment analysis here:
    # https://textblob.readthedocs.io/en/dev/quickstart.html#sentiment-analysis
    # You will not be able to do this without reading the documentation.

    # Brief instructions:
    # Create a TextBlob object.
    # Populate it to with the review (as a string not as a list of words).
    # Get the first element of its sentiment variable.
    # If it's more than the threshold 0, return pos. Otherwise return neg.
    temp = TextBlob(str(review))
    result = temp.sentiment.polarity
    if result > 0:
        return "pos"
    #########################################
    ##### YOUR PART D CODE ENDS HERE ########
    #########################################
    return "neg"


## FUNCTION FOR CALCULATING THE ACCURACY OF YOUR MODELS
# You do not need to modify this code.

def calculate_accuracy():
    keywordscorrect = 0
    nbcorrect = 0
    smnbcorrect = 0
    tbcorrect = 0
    affcorrect = 0

    # read in the test reviews
    testdata = glob.glob("test/*")
    for filename in testdata:
        wholereview = ""
        reviewwords = []
        with open(filename, encoding='utf8') as f:
            wholereview = f.read().rstrip()
        words = set(wholereview.split())
        for w in words:
            if w not in stops:
                reviewwords.append(w)
            
        # read the file name of the file to determine if its pos or neg
        filepolarity = re.sub(r"^.*?(pos|neg)-.*?$", r"\1", filename)
    
        # apply each classifier to that review, and check to see it's correct
        #file polarity- what it actually is  
        if filepolarity == user_defined_keywords(reviewwords):
            keywordscorrect += 1
        
    
        if filepolarity == naive_bayes(reviewwords):
            nbcorrect += 1

        if filepolarity == smooth_naive_bayes(reviewwords):
            smnbcorrect += 1
        

        if filepolarity == calculate_textblob(reviewwords):
            tbcorrect += 1

    # report the accuracy of each classifier
    print("User keyword accuracy: ", (keywordscorrect/200))
    print("Naive Bayes accuracy: ", (nbcorrect/200))
    print("Snoothed Naive Bayes accuracy: ", (smnbcorrect/200))
    print("TextBlob accuracy: ", (tbcorrect/200))



#####################
### RUN ALL TESTS ###
#####################

# You do not need to modify this code.

# read in the training data to get all the positive and negative words
poswords, negwords = read_in_training_data()

# calculate the naive bayes probabilities
poswordprobs, negwordprobs = calculate_nb_probabilities()

# calculate smoothed naive bayes probabilities
smooth_poswordprobs, smooth_negwordprobs = calculate_smooth_nb_probabilities()

# calculate the accuracy of all three approaches
calculate_accuracy()

