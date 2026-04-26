package com.factory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.factory.entity.SysAnnouncement;
import com.factory.mapper.SysAnnouncementMapper;
import com.factory.service.SysAnnouncementService;
import org.springframework.stereotype.Service;

@Service
public class SysAnnouncementServiceImpl extends ServiceImpl<SysAnnouncementMapper, SysAnnouncement> implements SysAnnouncementService {
}