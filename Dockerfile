FROM finalduty/archlinux

RUN pacman --noconfirm -Syu
RUN pacman --noconfirm -S base-devel asciidoc git

WORKDIR /
RUN git clone https://github.com/ioi/isolate.git
WORKDIR /isolate
RUN make
RUN cp default.cf /usr/local/etc/isolate

CMD ./isolate --init -b 1 && ./isolate --run -b 1 --dir=/usr/bin /usr/bin/id
