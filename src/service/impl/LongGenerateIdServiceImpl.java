package service.impl;

import service.GenerateIdService;

public class LongGenerateIdServiceImpl implements GenerateIdService<Long> {

    private long id = 0L;

    @Override
    public Long generateId() {
        return ++id;
    }

    @Override
    public Long getId(){
        return id;
    }
}
