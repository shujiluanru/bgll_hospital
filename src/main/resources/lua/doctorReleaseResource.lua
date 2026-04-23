local key=KEYS[1]
local doctorId=tonumber(ARGV[1])
local addNumber=tonumber(ARGV[2])
redis.call("HINCRBY",key,doctorId,addNumber)