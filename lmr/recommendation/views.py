from django.shortcuts import render

import numpy as np
import pandas as pd
from sklearn.feature_extraction import text
from sklearn.metrics.pairwise import cosine_similarity

import warnings
warnings.filterwarnings('ignore')

# 사용자 정보 불러오기
# Client = 

# 1. 알러지 정보 제외하고 추천


# 2. 사용자가 좋아요/위시리스트에 담은 음식 기준으로 추천

# 3. 

# 1. 사용자에게 알러지가 있는 경우, 제외하고 추천

# 2. 사용자가 좋아요한 음식들 + 위시리스트에 넣은 음식점들 반영

# 3. 최근 일주일 동안 사용자가 먹지 않은 음식 추천