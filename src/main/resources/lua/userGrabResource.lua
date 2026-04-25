local key1 = KEYS[1]
local reservation = KEYS[2]
local doctorId = ARGV[1]
local userId=ARGV[2]
local resource = tonumber(redis.call('hget', key1, doctorId))
if resource == nil or resource <= 0 then
    return -1  -- 资源不足或不存在
end
local reservationKey = userId .. ":" .. doctorId
-- 检查是否预约的同个医生
local e = redis.call('hsetnx', reservation, reservationKey,1)
if e ==0 then
    return -2  -- 已预约过
end

-- 原子操作：扣减资源
local d = redis.call('hincrby', key1, doctorId, -1)
return d